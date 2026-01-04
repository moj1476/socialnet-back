package org.socialnet.socialnet.shared.application.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication(
        scanBasePackages = {
                "org.socialnet.socialnet",
        }
)
@EnableJpaRepositories(basePackages = {
        "org.socialnet.socialnet.post.infrastructure.persistence.jpa.repository",
        "org.socialnet.socialnet.user.infrastructure.persistence.jpa.repository",
        "org.socialnet.socialnet.message.infrastructure.persistence.jpa.repository",
})
@EntityScan(basePackages = {
        "org.socialnet.socialnet.post.infrastructure.persistence.jpa.entity",
        "org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity",
        "org.socialnet.socialnet.message.infrastructure.persistence.jpa.entity",
})
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableCaching
public class SocialnetApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialnetApplication.class, args);
    }

}
