package com.example.media_service.application;

import com.example.media_service.domain.MediaRepository;
import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MediaService {
    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }
    public long getFileSize(String fileName) throws Exception {
        return mediaRepository.getFileSize(fileName);
    }

    public InputStream downloadFilePart(String fileName, long offset, long length) throws Exception {
        return mediaRepository.downloadFilePart(fileName, offset, length);
    }

    public String uploadFile(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String fileName = UUID.randomUUID() + extension;
        mediaRepository.uploadFile(file, fileName);
        return fileName;

    }

    public InputStream downloadFile(String fileName) throws Exception {
        return mediaRepository.downloadFile(fileName);
    }
}