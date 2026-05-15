package com.example.media_service.controllers;

import com.example.media_service.application.MinioService;
import io.minio.errors.ErrorResponseException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/medias")
public class MediaController {
    private final MinioService minioService;

    public MediaController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam("file") List<MultipartFile> files) {
        try {
            List<String> result = new ArrayList<>();
            for (MultipartFile file : files) {
                String name = minioService.uploadFile(file);
                result.add(name);
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String filename,
            @RequestHeader HttpHeaders headers // Добавляем получение заголовков запроса
    ) {
        try {
            long fileSize = minioService.getFileSize(filename);
            String mimeType = URLConnection.guessContentTypeFromName(filename);
            String contentType = (mimeType != null) ? mimeType : "application/octet-stream";
            List<HttpRange> ranges = headers.getRange();
            if (!ranges.isEmpty()) {
                HttpRange range = ranges.getFirst();
                long start = range.getRangeStart(fileSize);
                long end = range.getRangeEnd(fileSize);
                long contentLength = end - start + 1;

                InputStream streamPart = minioService.downloadFilePart(filename, start, contentLength);

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                        .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize)
                        .body(new InputStreamResource(streamPart));

            } else {
                InputStream stream = minioService.downloadFile(filename);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(new InputStreamResource(stream));
            }

        } catch (ErrorResponseException e) {
            if (e.response().code() == 404) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}