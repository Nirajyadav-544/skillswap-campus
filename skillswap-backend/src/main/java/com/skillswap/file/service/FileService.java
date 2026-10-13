package com.skillswap.file.service;

import com.skillswap.common.constant.FileConstants;
import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.common.util.FileUtil;
import com.skillswap.file.dto.FileResponse;
import com.skillswap.file.dto.FileUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileService {

    private final Path storagePath;

    @Value("${app.file.base-url:" + FileConstants.FILE_URL_PREFIX + "}")
    private String fileBaseUrl;

    public FileService(Path fileStoragePath) {
        this.storagePath = fileStoragePath;
    }

    /**
     * Uploads a file to the configured storage directory.
     */
    public FileUploadResponse uploadFile(MultipartFile file) {

        validateFile(file);

        String originalFileName =
                FileUtil.sanitizeFileName(file.getOriginalFilename());

        String storedFileName =
                FileUtil.generateStoredFileName(file);

        try {
            Path storedPath = FileUtil.storeFile(
                    file,
                    storagePath,
                    storedFileName
            );

            String extension =
                    FileUtil.getExtension(originalFileName);

            String fileUrl =
                    buildFileUrl(storedFileName);

            return new FileUploadResponse(
                    null,
                    originalFileName,
                    fileUrl,
                    file.getContentType(),
                    Files.size(storedPath),
                    extension,
                    "File uploaded successfully"
            );

        } catch (IOException e) {
            throw new BadRequestException(
                    "Unable to store file",
                    e
            );
        }
    }

    /**
     * Returns file metadata for a stored filename.
     */
    public FileResponse getFile(String storedFileName) {

        String safeFileName =
                sanitizeStoredFileName(storedFileName);

        Path filePath =
                storagePath.resolve(safeFileName).normalize();

        if (!FileUtil.exists(filePath)) {
            throw new ResourceNotFoundException(
                    "File not found"
            );
        }

        try {
            String originalFileName = safeFileName;
            String extension =
                    FileUtil.getExtension(safeFileName);

            String contentType =
                    Files.probeContentType(filePath);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return new FileResponse(
                    null,
                    originalFileName,
                    safeFileName,
                    buildFileUrl(safeFileName),
                    contentType,
                    Files.size(filePath),
                    extension,
                    null
            );

        } catch (IOException e) {
            throw new BadRequestException(
                    "Unable to read file",
                    e
            );
        }
    }

    /**
     * Reads a stored file as bytes.
     */
    public byte[] readFile(String storedFileName) {

        String safeFileName =
                sanitizeStoredFileName(storedFileName);

        Path filePath =
                storagePath.resolve(safeFileName).normalize();

        if (!FileUtil.exists(filePath)) {
            throw new ResourceNotFoundException(
                    "File not found"
            );
        }

        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new BadRequestException(
                    "Unable to read file",
                    e
            );
        }
    }

    /**
     * Deletes a stored file.
     */
    public void deleteFile(String storedFileName) {

        String safeFileName =
                sanitizeStoredFileName(storedFileName);

        Path filePath =
                storagePath.resolve(safeFileName).normalize();

        if (!FileUtil.exists(filePath)) {
            throw new ResourceNotFoundException(
                    "File not found"
            );
        }

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new BadRequestException(
                    "Unable to delete file",
                    e
            );
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null) {
            throw new BadRequestException(
                    "File is required"
            );
        }

        if (file.isEmpty()) {
            throw new BadRequestException(
                    "File cannot be empty"
            );
        }

        if (file.getSize() > FileConstants.MAX_FILE_SIZE) {
            throw new BadRequestException(
                    "File size cannot exceed "
                            + FileUtil.formatFileSize(
                            FileConstants.MAX_FILE_SIZE
                    )
            );
        }

        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null
                || originalFileName.isBlank()) {
            throw new BadRequestException(
                    "File name is required"
            );
        }

        if (originalFileName.length()
                > FileConstants.MAX_FILE_NAME_LENGTH) {
            throw new BadRequestException(
                    "File name is too long"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || contentType.isBlank()) {
            throw new BadRequestException(
                    "File content type is required"
            );
        }
    }

    private String sanitizeStoredFileName(
            String storedFileName
    ) {
        if (storedFileName == null
                || storedFileName.isBlank()) {
            throw new BadRequestException(
                    "File name is required"
            );
        }

        String safeFileName =
                FileUtil.sanitizeFileName(storedFileName);

        if (!safeFileName.equals(storedFileName)) {
            throw new BadRequestException(
                    "Invalid file name"
            );
        }

        return safeFileName;
    }

    private String buildFileUrl(
            String storedFileName
    ) {
        String baseUrl = fileBaseUrl;

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        return baseUrl + storedFileName;
    }
}