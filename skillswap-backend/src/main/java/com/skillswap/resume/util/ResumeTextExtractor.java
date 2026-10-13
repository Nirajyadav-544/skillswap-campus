package com.skillswap.resume.util;

import com.skillswap.common.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public final class ResumeTextExtractor {

    private ResumeTextExtractor() {
    }

    public static String extractText(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Resume file is required");
        }

        String extension = ResumeFileValidator.getExtension(
                file.getOriginalFilename()
        );

        try {
            /*
             * PDF/DOC/DOCX extraction is intentionally kept behind
             * this utility so the storage/upload layer does not depend
             * on a particular parser implementation.
             *
             * Apache PDFBox / Apache POI can be connected here later.
             */
            return extractUsingAvailableParser(file, extension);

        } catch (IOException exception) {
            throw new BadRequestException(
                    "Unable to read resume file"
            );
        }
    }

    private static String extractUsingAvailableParser(
            MultipartFile file,
            String extension
    ) throws IOException {

        /*
         * Do not silently invent extracted content.
         *
         * Until PDFBox/Apache POI is present in pom.xml, return an
         * empty string rather than pretending that binary resume
         * content has been successfully extracted.
         */
        if (!isSupportedExtension(extension)) {
            throw new BadRequestException(
                    "Unsupported resume file type"
            );
        }

        try (InputStream inputStream = file.getInputStream()) {
            if (inputStream.read() == -1) {
                return "";
            }
        }

        return "";
    }

    private static boolean isSupportedExtension(
            String extension
    ) {

        if (extension == null) {
            return false;
        }

        String normalized = extension
                .trim()
                .toLowerCase(Locale.ROOT);

        return normalized.equals("pdf")
                || normalized.equals("doc")
                || normalized.equals("docx");
    }
}