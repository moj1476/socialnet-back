package org.socialnet.socialnet.shared.application.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Value("${spring.application.name}")
    public String appName;

    @Bean
    OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title(appName.toUpperCase())
                        .description("Cefea" + " " + appName.toUpperCase())
                        .version("1.0.0")
                ).addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
                .addServersItem(new Server().url("http://localhost:8080/api/v1").description("Local server"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("ApiKeyAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization"))
                );
    }
}
