package org.socialnet.socialnet.shared.core.port.output;

import java.io.InputStream;

public interface FileStoragePort {
    String save(InputStream inputStream, String originalFilename, String subfolder);

    String save(InputStream inputStream, String originalFilename, String subfolder, String fileToRemove);
}
