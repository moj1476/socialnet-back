package org.socialnet.socialnet.shared.application.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String storageLocation;
    private final String fileServePath;

    public WebConfig(
            @Value("${file.storage.location:uploads}") String storageLocation,
            @Value("${file.storage.serve-path:/files}") String fileServePath
    ) {
        this.storageLocation = storageLocation;
        this.fileServePath = fileServePath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(storageLocation);
        //String uploadPath = uploadDir.toFile().getAbsolutePath();
        //registry.addResourceHandler(fileServePath + "/**")
        //                .addResourceLocations("file:/" + uploadPath + "/");

        registry.addResourceHandler(fileServePath + "/**")
                .addResourceLocations(uploadDir.toUri().toString());
    }
}
