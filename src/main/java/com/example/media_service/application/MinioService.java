package com.example.media_service.application;

import com.example.media_service.domain.MediaType;
import com.example.media_service.domain.repositories.MediaRepository;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct; // Для Spring Boot 3.x. Если у тебя 2.x, используй javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

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

    // Этот метод выполнится один раз при старте приложения
    @PostConstruct
    public void init() {
        try {
            boolean isExist = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build());
            if (!isExist) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build());
                System.out.println("Бакет " + bucketName + " успешно создан.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при проверке/создании бакета MinIO: " + bucketName, e);
        }
    }

    public String uploadFile(MultipartFile file) throws Exception {
        // Рекомендация: лучше генерировать уникальное имя, чтобы файлы с одинаковыми именами не перезаписывали друг друга
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String fileName = UUID.randomUUID()+ extension;

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

        if (type != null) {
            // Не забудь обновить путь до файла, если стал использовать UUID
            // minioMediaRepository.insertMediaAsync(fileName, type.name(), "/api/medias/download/" + fileName);
            return fileName;
        } else {
            throw new RuntimeException("Не удалосьопределить тип");
        }
    }

    public InputStream downloadFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
    }
}