package com.example.media_service.services;

import com.example.media_service.models.MediaType;
import com.example.media_service.repositories.MediaRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class MinioService {
    @Value("${minio.bucket.name}")
    private String bucketName;


    private final MinioClient minioClient;
    private final MediaRepository mediaRepository;

    public MinioService(MinioClient minioClient, MediaRepository mediaRepository) {
        this.minioClient = minioClient;
        this.mediaRepository = mediaRepository;
    }
    @Async
    public void uploadFile(MultipartFile file) throws Exception {

            String fileName = file.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            MediaType type = switch (Objects.requireNonNull(file.getContentType()).split("/")[0]) {
                case "audio" -> MediaType.AUDIO;
                case "video" -> MediaType.VIDEO;
                case "image" -> MediaType.PHOTO;
                default -> null;
            };
            if (type != null){
                mediaRepository.insertMediaAsync(fileName,type.name(),"/api/medias/download/"+fileName);

            }

    }
    @Async
    public CompletableFuture<InputStream> downloadFile(String fileName) throws Exception {
        return CompletableFuture.completedFuture(
                minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()));
    }
}