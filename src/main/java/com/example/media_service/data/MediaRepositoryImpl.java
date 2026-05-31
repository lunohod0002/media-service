package com.example.media_service.data;

import com.example.media_service.domain.MediaRepository;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
@Repository
public class MediaRepositoryImpl implements MediaRepository {
    @Value("${minio.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;

    public MediaRepositoryImpl(MinioClient minioClient) {
        this.minioClient=minioClient;
    }
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
    @Override
    public long getFileSize(String fileName) throws Exception {
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
        return stat.size();
    }

    @Override
    public InputStream downloadFilePart(String fileName, long offset, long length) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .offset(offset)
                        .length(length)
                        .build()
        );
    }

    @Override
    public void uploadFile(MultipartFile file, String fileName) throws Exception{
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
    }

    @Override
    public InputStream downloadFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
    }
}
