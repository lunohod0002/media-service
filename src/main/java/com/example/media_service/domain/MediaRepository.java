package com.example.media_service.domain;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

public interface MediaRepository {
    long getFileSize(String fileName) throws Exception;
    InputStream downloadFilePart(String fileName, long offset, long length) throws Exception;

    void uploadFile(MultipartFile file, String fileName) throws Exception;
    InputStream downloadFile(String fileName) throws Exception;

}
