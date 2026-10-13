package com.skillswap.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public final class FileUtil {

    private FileUtil() {
    }

    /**
     * Creates the storage directory if it does not already exist.
     */
    public static Path createDirectory(String directory) throws IOException {
        Path path = Paths.get(directory).toAbsolutePath().normalize();

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        return path;
    }

    /**
     * Generates a unique stored filename while preserving the extension.
     */
    public static String generateStoredFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        String extension = getExtension(originalFileName);

        String uniqueName = UUID.randomUUID().toString();

        if (extension.isEmpty()) {
            return uniqueName;
        }

        return uniqueName + "." + extension;
    }

    /**
     * Extracts the file extension without the dot.
     */
    public static String getExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "";
        }

        String cleanName = Paths.get(fileName)
                .getFileName()
                .toString();

        int lastDot = cleanName.lastIndexOf('.');

        if (lastDot <= 0 || lastDot == cleanName.length() - 1) {
            return "";
        }

        return cleanName.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Returns a safe original filename.
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "file";
        }

        String cleanName = Paths.get(fileName)
                .getFileName()
                .toString();

        return cleanName
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .trim();
    }

    /**
     * Stores the uploaded file in the supplied directory.
     */
    public static Path storeFile(
            MultipartFile file,
            Path storageDirectory,
            String storedFileName
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty");
        }

        createDirectory(storageDirectory.toString());

        Path targetPath = storageDirectory
                .resolve(storedFileName)
                .normalize();

        if (!targetPath.getParent().equals(storageDirectory.toAbsolutePath().normalize())) {
            throw new IOException("Invalid file path");
        }

        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return targetPath;
    }

    /**
     * Deletes a file if it exists.
     */
    public static boolean deleteFile(Path filePath) throws IOException {
        if (filePath == null) {
            return false;
        }

        return Files.deleteIfExists(filePath);
    }

    /**
     * Checks whether a file exists.
     */
    public static boolean exists(Path filePath) {
        return filePath != null && Files.exists(filePath);
    }

    /**
     * Returns the file size in bytes.
     */
    public static long getFileSize(Path filePath) throws IOException {
        return Files.size(filePath);
    }

    /**
     * Converts bytes into a readable size.
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }

        double kb = bytes / 1024.0;

        if (kb < 1024) {
            return String.format("%.2f KB", kb);
        }

        double mb = kb / 1024.0;

        if (mb < 1024) {
            return String.format("%.2f MB", mb);
        }

        double gb = mb / 1024.0;

        return String.format("%.2f GB", gb);
    }
}