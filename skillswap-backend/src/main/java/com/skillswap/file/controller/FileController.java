package com.skillswap.file.controller;

import com.skillswap.file.dto.FileResponse;
import com.skillswap.file.dto.FileUploadResponse;
import com.skillswap.file.service.FileService;
import com.skillswap.common.validation.ValidFile;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController    
@RequestMapping("/api/v1/files")
@Validated
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestPart("file")
            @ValidFile
            MultipartFile file
    ) {
        return ResponseEntity.ok(
                fileService.uploadFile(file)
        );
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<FileResponse> getFile(
            @PathVariable String fileName
    ) {
        return ResponseEntity.ok(
                fileService.getFile(fileName)
        );
    }

    @GetMapping("/{fileName}/download")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String fileName
    ) {
        FileResponse file =
                fileService.getFile(fileName);

        byte[] content =
                fileService.readFile(fileName);

        MediaType mediaType;

        try {
            mediaType = MediaType.parseMediaType(
                    file.getContentType()
            );
        } catch (Exception e) {
            mediaType =
                    MediaType.APPLICATION_OCTET_STREAM;
        }

        ContentDisposition disposition =
                ContentDisposition.attachment()
                        .filename(file.getStoredFileName())
                        .build();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(content.length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )
                .body(content);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String fileName
    ) {
        fileService.deleteFile(fileName);

        return ResponseEntity.noContent().build();
    }
}