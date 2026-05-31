package com.example.media_service;

import com.example.media_service.application.MediaService;
import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

	@Mock
	private MinioClient minioClient;

	@InjectMocks
	private MediaService mediaService;

	private static final String BUCKET_NAME = "test-bucket";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(mediaService, "bucketName", BUCKET_NAME);
	}

	@Test
	void getFileSize_shouldReturnSize() throws Exception {
		StatObjectResponse stat = mock(StatObjectResponse.class);
		when(stat.size()).thenReturn(1024L);
		when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(stat);

		long size = mediaService.getFileSize("file.mp4");

		assertEquals(1024L, size);
		verify(minioClient).statObject(any(StatObjectArgs.class));
	}

}