package com.example.algoproject.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String upload(MultipartFile multipartFile, String path) throws IOException {
        File uploadFile = convert(multipartFile).orElseThrow(()->new IllegalArgumentException("error: MultipartFile -> File convert fail..."));

        return upload(uploadFile, path);
    }

    // S3 로 파일 업로드하기
    private String upload(File uploadFile, String path) {
//        String fileName = path + UUID.randomUUID() + uploadFile.getName();
        String url = putS3(uploadFile, path + uploadFile.getName()); //uuid 필요성..?
        removeNewFile(uploadFile);
        return url;
    }

    // S3 로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // Local 에 저장된 파일 지우기
    private void removeNewFile(File target) {
        if (target.delete()) {
            log.info("Local File delete success");
            return;
        }
        log.info("Local File delete fail");
    }

    // Local 에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File("user" + file.getOriginalFilename());
        log.info("original name: " + file.getOriginalFilename());

        // 바로 위에서 지정한 경로에 File 이 생성됨
        if (convertFile.createNewFile()) {
            // 경로가 잘못되었다면 생성 불가능
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            log.info("file convert success");
            return Optional.of(convertFile);
        }
        log.info("file convert failed");
        return Optional.empty();
    }
}