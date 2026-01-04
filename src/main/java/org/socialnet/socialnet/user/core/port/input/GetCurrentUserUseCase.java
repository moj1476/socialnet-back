package org.socialnet.socialnet.user.core.port.input;

import org.socialnet.socialnet.user.core.model.User;

public interface GetCurrentUserUseCase {
    User handle(String userId);

}
