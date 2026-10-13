package com.skillswap.resume.util;

import com.skillswap.common.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;

public final class ResumeFileValidator {

    private ResumeFileValidator() {
    }

    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf",
            "doc",
            "docx"
    );

    public static void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    "Resume file is required"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException(
                    "Resume file must not exceed 10 MB"
            );
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException(
                    "Resume file name is required"
            );
        }

        String extension = getExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                    "Only PDF, DOC and DOCX resume files are allowed"
            );
        }

        String contentType = file.getContentType();

        if (contentType != null
                && !contentType.isBlank()
                && !ALLOWED_CONTENT_TYPES.contains(
                        contentType.toLowerCase(Locale.ROOT)
                )) {

            throw new BadRequestException(
                    "Unsupported resume file type"
            );
        }
    }

    public static String getExtension(String filename) {

        if (filename == null || filename.isBlank()) {
            return "";
        }

        int lastDot = filename.lastIndexOf('.');

        if (lastDot < 0 || lastDot == filename.length() - 1) {
            return "";
        }

        return filename
                .substring(lastDot + 1)
                .toLowerCase(Locale.ROOT);
    }

    public static boolean isSupported(MultipartFile file) {

        try {
            validate(file);
            return true;
        } catch (BadRequestException exception) {
            return false;
        }
    }

    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
}