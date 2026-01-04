package org.socialnet.socialnet.auth.application.configuration;

import org.socialnet.socialnet.auth.core.port.input.SecurityPortUseCase;
import org.socialnet.socialnet.auth.core.port.output.AuthUseCase;
import org.socialnet.socialnet.auth.core.usecase.SecurityPortUseCaseImpl;
import org.socialnet.socialnet.auth.infrastructure.external.KeycloakSecurityAdapter;
import org.socialnet.socialnet.user.core.port.output.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public SecurityPortUseCase securityPortUseCase(AuthUseCase keycloakSecurityAdapter, UserRepository userRepository) {
        return new SecurityPortUseCaseImpl(keycloakSecurityAdapter, userRepository);
    }

}
