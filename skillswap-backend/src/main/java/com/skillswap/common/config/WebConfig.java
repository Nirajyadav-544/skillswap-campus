package com.skillswap.common.config;

import com.skillswap.common.constant.FileConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.file.storage-path:" + FileConstants.DEFAULT_STORAGE_DIRECTORY + "}")
    private String storagePath;

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {

        Path path = Paths.get(storagePath)
                .toAbsolutePath()
                .normalize();

        String location =
                path.toUri().toString();

        if (!location.endsWith("/")) {
            location += "/";
        }

        registry.addResourceHandler(
                        FileConstants.FILE_URL_PREFIX + "**"
                )
                .addResourceLocations(location);
    }
}