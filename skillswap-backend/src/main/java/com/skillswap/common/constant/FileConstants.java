package com.skillswap.common.constant;

public final class FileConstants {

    private FileConstants() {
    }

    /*
     * Default storage directory.
     *
     * Can be overridden through application.properties:
     *
     * skillswap.file.storage-dir=uploads
     */
    public static final String DEFAULT_STORAGE_DIRECTORY = "uploads";

    /*
     * Maximum file size:
     * 10 MB
     */
    public static final long MAX_FILE_SIZE =
            10 * 1024 * 1024L;

    /*
     * Maximum filename length.
     */
    public static final int MAX_FILE_NAME_LENGTH = 255;

    /*
     * URL prefix used by the file module.
     */
    public static final String FILE_URL_PREFIX = "/files/";

    /*
     * =========================
     * IMAGE
     * =========================
     */
    public static final String CONTENT_TYPE_JPEG =
            "image/jpeg";

    public static final String CONTENT_TYPE_PNG =
            "image/png";

    public static final String CONTENT_TYPE_GIF =
            "image/gif";

    public static final String CONTENT_TYPE_WEBP =
            "image/webp";

    /*
     * =========================
     * PDF
     * =========================
     */
    public static final String CONTENT_TYPE_PDF =
            "application/pdf";

    /*
     * =========================
     * MICROSOFT WORD
     * =========================
     */
    public static final String CONTENT_TYPE_DOC =
            "application/msword";

    public static final String CONTENT_TYPE_DOCX =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    /*
     * =========================
     * MICROSOFT EXCEL
     * =========================
     */
    public static final String CONTENT_TYPE_XLS =
            "application/vnd.ms-excel";

    public static final String CONTENT_TYPE_XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /*
     * =========================
     * MICROSOFT POWERPOINT
     * =========================
     */
    public static final String CONTENT_TYPE_PPT =
            "application/vnd.ms-powerpoint";

    public static final String CONTENT_TYPE_PPTX =
            "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    /*
     * =========================
     * AUDIO
     * =========================
     */
    public static final String CONTENT_TYPE_MP3 =
            "audio/mpeg";

    public static final String CONTENT_TYPE_WAV =
            "audio/wav";

    public static final String CONTENT_TYPE_OGG_AUDIO =
            "audio/ogg";

    public static final String CONTENT_TYPE_M4A =
            "audio/mp4";

    /*
     * =========================
     * VIDEO
     * =========================
     */
    public static final String CONTENT_TYPE_MP4 =
            "video/mp4";

    public static final String CONTENT_TYPE_WEBM =
            "video/webm";

    public static final String CONTENT_TYPE_OGG_VIDEO =
            "video/ogg";

    /*
     * =========================
     * TEXT
     * =========================
     */
    public static final String CONTENT_TYPE_TEXT =
            "text/plain";
}