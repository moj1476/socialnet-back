package org.socialnet.socialnet.message.core.port.input;

public interface GetCallTokenUseCase {
    String execute(String userId, String chatId);
}