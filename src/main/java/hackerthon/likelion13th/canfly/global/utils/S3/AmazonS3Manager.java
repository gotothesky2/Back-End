package hackerthon.likelion13th.canfly.global.utils.S3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) {
        String fileName = generateFileName(file);
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            log.info("File uploaded to S3: {}", fileName);
            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            log.error("Error occurred while uploading file to S3", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    public void deleteFile(String fileKey) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileKey));
            log.info("Deleted file from S3: {}", fileKey);
        } catch (SdkClientException e) {
            log.error("Error occurred while deleting file from S3: {}", fileKey, e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }
        return UUID.randomUUID() + "-" + originalFilename;
    }

    public MediaType contentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "txt" -> MediaType.TEXT_PLAIN;
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "pdf" -> MediaType.APPLICATION_PDF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    public Optional<File> convert(MultipartFile file) throws IOException { // 파일로 변환
        File convertedFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + file.getOriginalFilename());
        file.transferTo(convertedFile);
        return Optional.of(convertedFile);
    }

    public String putS3(java.io.File uploadFile, String fileName) { // S3로 업로드
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }
}