package com.huangydyn.infustructure;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.huangydyn.config.AmazonS3Configuration;
import com.huangydyn.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class FileClient {

    private final AmazonS3 amazonS3;

    private final String bucketName;

    public FileClient(AmazonS3Configuration config) {
        this.amazonS3 = initialS3Client(config);
        this.bucketName = config.getBucketName();
    }

    private AmazonS3 initialS3Client(AmazonS3Configuration config) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(config.getAccessKey(),
                config.getSecretKey());
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_EAST_1)
                .build();
    }

    public String uploadFile(byte[] data) {
        try (InputStream fileStream = new ByteArrayInputStream(data)) {
            String fileName = generateFileName(data);
            String fileKey = generateFileKey(fileName);
            log.info("[Upload File] start to upload file with key {}", fileKey);

            amazonS3.putObject(bucketName, fileKey, fileStream, new ObjectMetadata());
            log.info("[Upload File] finish to upload file with key {}", fileKey);
            return fileName;
        } catch (Exception e) {
            log.error("[Upload File] upload file failed", e);
            throw new RuntimeException("Upload met error");
        }
    }

    public InputStream downloadFile(String fileName) {
        try {
            String fileKey = generateFileKey(fileName);
            log.info("[Download File] start to download file with key {}", fileKey);

            S3Object s3Object = amazonS3.getObject(bucketName, fileKey);
            log.info("[Download File] finish to download file with key {}", fileKey);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("[Download File] download file failed", e);
            throw new RuntimeException("met error");
        }
    }

    public String getPreSignedUrl(String fileName) {
        try {
            String fileKey = generateFileKey(fileName);
            log.info("[Get PreSignedUrl] start to get url with key {}", fileKey);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, fileKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(getExpiration());
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            log.info("[Get PreSignedUrl] finish to get url with key {}", fileKey);
            return url.toString();
        } catch (Exception e) {
            log.error("[Get PreSignedUrl] download file failed", e);
            throw new RuntimeException("met error");
        }
    }

    private Date getExpiration() {
        Date expiration = new Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        //60s expired
        expTimeMillis += 1000 * 60;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    private String generateFileName(byte[] data) throws MimeTypeException {
        return String.format("%s%s", UUID.randomUUID().toString(), FileUtils.getFileExtension(data));
    }

    private String generateFileKey(String fileName) {
        return String.format("%s/%s", "test01", fileName);
    }
}
