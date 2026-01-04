package org.socialnet.socialnet.message.infrastructure.external;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.message.core.port.output.CallTokenGeneratorPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LiveKitTokenAdapter implements CallTokenGeneratorPort {

    @Value("${livekit.api-key:devkey}")
    private String apiKey;

    @Value("${livekit.api-secret:secret}")
    private String apiSecret;

    @Override
    public String generateToken(String userId, String username, String chatId) {
        Algorithm algorithm = Algorithm.HMAC256(apiSecret);

        Map<String, Object> videoGrants = Map.of(
                "roomJoin", true,
                "room", chatId,
                "canPublish", true,
                "canSubscribe", true
        );

        return JWT.create()
                .withIssuer(apiKey)
                .withSubject(userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .withNotBefore(new Date())
                .withClaim("video", videoGrants)
                .withClaim("name", username)
                .withClaim("identity", userId)
                .sign(algorithm);
    }
}