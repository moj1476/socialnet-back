package org.socialnet.socialnet.shared.infrastructure.external;

import org.socialnet.socialnet.shared.core.port.output.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileStorage implements FileStoragePort {

    private final Path rootLocation;
    private final String fileServePath;

    public FileStorage(
            @Value("${file.storage.location:uploads}") String storageLocation,
            @Value("${file.storage.serve-path:/files}") String fileServePath
    ) {
        this.rootLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
        this.fileServePath = fileServePath;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для хранения файлов.", e);
        }
    }

    @Override
    public String save(InputStream inputStream, String originalFilename, String subfolder) {
        return save(inputStream, originalFilename, subfolder, null);
    }

    @Override
    public String save(InputStream inputStream, String originalFilename, String subfolder, String fileToRemove) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + "." + extension;

        try {
            if (inputStream == null) {
                throw new IllegalArgumentException("InputStream не может быть null");
            }
            if(fileToRemove != null) {
                Path fileToRemovePath = this.rootLocation.resolve(fileToRemove.replace(fileServePath, "").substring(1)).normalize().toAbsolutePath();
                if (Files.exists(fileToRemovePath)) {
                    Files.delete(fileToRemovePath);
                }
            }

            Path subfolderPath = this.rootLocation.resolve(subfolder);
            Files.createDirectories(subfolderPath);

            Path destinationFile = subfolderPath.resolve(uniqueFilename)
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(subfolderPath.toAbsolutePath())) {
                throw new SecurityException("Невозможно сохранить файл за пределами целевой директории.");
            }

            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return StringUtils.trimTrailingCharacter(fileServePath, '/') + "/" + subfolder + "/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении файла " + originalFilename, e);
        }
    }
}
