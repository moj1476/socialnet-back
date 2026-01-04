package org.socialnet.socialnet.message.application.configuration;

import org.socialnet.socialnet.message.core.port.input.GetCallTokenUseCase;
import org.socialnet.socialnet.message.core.port.output.CallTokenGeneratorPort;
import org.socialnet.socialnet.message.core.usecase.GetCallTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CallUseCaseConfig {
    @Bean
    public GetCallTokenUseCase getCallTokenUseCase(CallTokenGeneratorPort tokenGeneratorPort) {
        return new GetCallTokenService(tokenGeneratorPort);
    }
}