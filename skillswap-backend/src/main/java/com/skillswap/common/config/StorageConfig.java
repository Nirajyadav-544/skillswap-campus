package com.skillswap.common.config;

import com.skillswap.common.constant.FileConstants;
import com.skillswap.common.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;

@Configuration
public class StorageConfig {

    @Value("${app.file.storage-path:" + FileConstants.DEFAULT_STORAGE_DIRECTORY + "}")
    private String storagePath;

    @Bean
    public Path fileStoragePath() throws IOException {
        return FileUtil.createDirectory(storagePath);
    }
}