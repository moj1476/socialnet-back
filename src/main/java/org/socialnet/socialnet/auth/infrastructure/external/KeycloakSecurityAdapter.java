package org.socialnet.socialnet.auth.infrastructure.external;

import org.socialnet.socialnet.auth.core.model.AuthenticatedUser;
import org.socialnet.socialnet.auth.core.model.JwtTokens;
import org.socialnet.socialnet.auth.core.port.output.AuthUseCase;
import org.socialnet.socialnet.auth.infrastructure.external.dto.KeycloakAuthResponse;
import org.socialnet.socialnet.auth.infrastructure.external.mapper.KeycloakAuthMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Component
public class KeycloakSecurityAdapter implements AuthUseCase {

    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    public static final String PASSWORD_GRANT_TYPE = "password";
    public static final String REFRESH_GRANT_TYPE = "refresh_token";

    @Value("${keycloak.admin-users-uri}")
    private String adminUsersUri;

    @Value("${spring.security.oauth2.authorizationserver.endpoint.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${keycloak.token-uri}")
    private String keycloakUrl;

    @Value("${keycloak.token-revoke}")
    private String keycloakTokenRevokeUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final KeycloakAuthMapper keycloakAuthMapper;

    public KeycloakSecurityAdapter(KeycloakAuthMapper keycloakAuthMapper) {
        this.restTemplate = new RestTemplate();
        this.keycloakAuthMapper = keycloakAuthMapper;
    }

    @Override
    public JwtTokens registerNewUser(String login, String email, String password) {
        String adminToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> userRepresentation = Map.of(
                "username", login,
                "email", email,
                "firstName", "not_set",
                "lastName", "not_set",
                "enabled", true,
                "credentials", List.of(
                        Map.of(
                                "type", "password",
                                "value", password,
                                "temporary", false
                        )
                )
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(userRepresentation, headers);

        String createUserUrl = adminUsersUri;

        try {
            restTemplate.postForEntity(createUserUrl, requestEntity, Object.class);
            return login(login, password);
        } catch (HttpClientErrorException.Conflict e) {
            String errorMessage = "Пользователь с такой почтой или логином уже существует.";
            throw new RuntimeException(errorMessage);
        } catch (Exception e) {
            throw new RuntimeException("Произошла непредвиденная ошибка при создании пользователя в Keycloak", e);
        }
    }

    @Override
    public JwtTokens login(String login, String password) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add(CLIENT_ID, clientId);
        paramMap.add(CLIENT_SECRET, clientSecret);
        paramMap.add("username", login);
        paramMap.add("password", password);
        paramMap.add(GRANT_TYPE, PASSWORD_GRANT_TYPE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(paramMap, headers);

        try {
            ResponseEntity<KeycloakAuthResponse> response = restTemplate.exchange(
                    keycloakUrl,
                    HttpMethod.POST,
                    entity,
                    KeycloakAuthResponse.class
            );

            KeycloakAuthResponse tokenResponse = response.getBody();

            if (tokenResponse == null) {
                throw new RuntimeException("Ошибка аутентификации: пустое тело запроса");
            }

            return keycloakAuthMapper.toDomain(tokenResponse);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Неправильный логин или пароль", e);
            }
            throw new RuntimeException("Ошибка аутентификации", e);
        }
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        revokeToken(refreshToken);
        revokeToken(accessToken);
    }

    private void revokeToken(String token) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add(CLIENT_ID, clientId);
        paramMap.add(CLIENT_SECRET, clientSecret);
        paramMap.add("token", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(paramMap, headers);

        try {
            ResponseEntity<KeycloakAuthResponse> response = restTemplate.exchange(
                    keycloakTokenRevokeUrl,
                    HttpMethod.POST,
                    entity,
                    KeycloakAuthResponse.class
            );

            KeycloakAuthResponse tokenResponse = response.getBody();

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Ошибка аутентификации", e);
        }
    }

    @Override
    public Optional<AuthenticatedUser> getUserByToken(String token) {
        Jwt jwt = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build().decode(token);
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String login = jwt.getClaimAsString("preferred_username");
        List<String> roles = getRoles(jwt);
        return Optional.of(AuthenticatedUser.create(userId, email, login, Set.copyOf(roles)));
    }



    @Override
    public JwtTokens refreshToken(String token) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add(CLIENT_ID, clientId);
        paramMap.add(CLIENT_SECRET, clientSecret);
        paramMap.add("refresh_token", token);
        paramMap.add(GRANT_TYPE, REFRESH_GRANT_TYPE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(paramMap, headers);

        try {
            ResponseEntity<KeycloakAuthResponse> response = restTemplate.exchange(
                    keycloakUrl,
                    HttpMethod.POST,
                    entity,
                    KeycloakAuthResponse.class
            );

            KeycloakAuthResponse tokenResponse = response.getBody();

            if (tokenResponse == null) {
                throw new RuntimeException("Ошибка аутентификации: пустое тело запроса");
            }

            return keycloakAuthMapper.toDomain(tokenResponse);

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Ошибка аутентификации", e);
        }
    }

    private List<String> getRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

        if (resourceAccess != null && resourceAccess.containsKey("account")) {
            Map<String, Object> accountAccess = (Map<String, Object>) resourceAccess.get("account");

            if (accountAccess != null && accountAccess.containsKey("roles")) {
                List<String> roles = (List<String>) accountAccess.get("roles");
                return roles;
            }
        }

        return Collections.emptyList();
    }

    private String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        String tokenUrl = keycloakUrl;

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    @Override
    public void changePassword(String userId, String newPassword) {
        String adminToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> passwordCredentials = Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", false // false = пароль постоянный, true = потребует смены при входе
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(passwordCredentials, headers);

        String resetPasswordUrl = String.format("%s/%s/reset-password", adminUsersUri, userId);

        try {
            restTemplate.exchange(
                    resetPasswordUrl,
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Ошибка при изменении пароля пользователя в Keycloak: " + e.getMessage(), e);
        }
    }
}
