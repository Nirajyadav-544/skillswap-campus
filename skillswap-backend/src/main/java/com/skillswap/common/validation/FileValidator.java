package com.skillswap.common.validation;

import com.skillswap.common.constant.FileConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class FileValidator
        implements ConstraintValidator<ValidFile, MultipartFile> {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    // Images
                    FileConstants.CONTENT_TYPE_JPEG,
                    FileConstants.CONTENT_TYPE_PNG,
                    FileConstants.CONTENT_TYPE_GIF,
                    FileConstants.CONTENT_TYPE_WEBP,

                    // PDF
                    FileConstants.CONTENT_TYPE_PDF,

                    // Word
                    FileConstants.CONTENT_TYPE_DOC,
                    FileConstants.CONTENT_TYPE_DOCX,

                    // Excel
                    FileConstants.CONTENT_TYPE_XLS,
                    FileConstants.CONTENT_TYPE_XLSX,

                    // PowerPoint
                    FileConstants.CONTENT_TYPE_PPT,
                    FileConstants.CONTENT_TYPE_PPTX,

                    // Audio
                    FileConstants.CONTENT_TYPE_MP3,
                    FileConstants.CONTENT_TYPE_WAV,
                    FileConstants.CONTENT_TYPE_OGG_AUDIO,
                    FileConstants.CONTENT_TYPE_M4A,

                    // Video
                    FileConstants.CONTENT_TYPE_MP4,
                    FileConstants.CONTENT_TYPE_WEBM,
                    FileConstants.CONTENT_TYPE_OGG_VIDEO,

                    // Text
                    FileConstants.CONTENT_TYPE_TEXT
            );

    @Override
    public boolean isValid(
            MultipartFile file,
            ConstraintValidatorContext context
    ) {

        /*
         * @NotNull can be used separately
         * when null must be rejected.
         */
        if (file == null) {
            return true;
        }

        if (file.isEmpty()) {
            return false;
        }

        if (file.getSize()
                > FileConstants.MAX_FILE_SIZE) {

            return false;
        }

        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null
                || originalFileName.isBlank()) {

            return false;
        }

        if (originalFileName.length()
                > FileConstants.MAX_FILE_NAME_LENGTH) {

            return false;
        }

        String contentType =
                file.getContentType();

        if (contentType == null
                || contentType.isBlank()) {

            return false;
        }

        return ALLOWED_CONTENT_TYPES.contains(
                contentType
                        .toLowerCase()
                        .trim()
        );
    }
}