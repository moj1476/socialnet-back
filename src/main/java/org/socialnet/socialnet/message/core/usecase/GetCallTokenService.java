package org.socialnet.socialnet.message.core.usecase;

import lombok.RequiredArgsConstructor;
import org.socialnet.socialnet.message.core.port.input.GetCallTokenUseCase;
import org.socialnet.socialnet.message.core.port.output.CallTokenGeneratorPort;
import org.springframework.stereotype.Service;

public class GetCallTokenService implements GetCallTokenUseCase {

    private final CallTokenGeneratorPort tokenGeneratorPort;
    // private final ChatRepositoryPort chatRepository;
    public GetCallTokenService(CallTokenGeneratorPort tokenGeneratorPort) {
        this.tokenGeneratorPort = tokenGeneratorPort;
    }

    @Override
    public String execute(String userId, String chatId) {
        // TODO: Добавить проверку: userRepository.isMember(chatId, userId)

        String username = userId; 
        
        return tokenGeneratorPort.generateToken(userId, username, chatId);
    }
}