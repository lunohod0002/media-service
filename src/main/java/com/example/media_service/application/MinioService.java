package com.example.media_service.application;

import com.example.media_service.business.MediaType;
import com.example.media_service.business.repositories.MediaRepository;
import com.example.media_service.data.MinioMediaRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

@Service
public class MinioService {
    @Value("${minio.bucket.name}")
    private String bucketName;


    private final MinioClient minioClient;
    private final MediaRepository minioMediaRepository;

    public MinioService(MinioClient minioClient, MediaRepository minioMediaRepository) {
        this.minioClient = minioClient;
        this.minioMediaRepository = minioMediaRepository;
    }
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
                minioMediaRepository.insertMediaAsync(fileName,type.name(),"/api/medias/download/"+fileName);

            }

    }
    public InputStream downloadFile(String fileName) throws Exception {
        return
                minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
    }
}