package org.socialnet.socialnet.message.core.port.output;

public interface CallTokenGeneratorPort {
    String generateToken(String userId, String username, String chatId);
}