package com.myapp.guess_who.api;

import com.myapp.guess_who.utils.Utilities;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Client s3Client;
    private final Utilities utilities;

    @Value("${custom.aws.s3.bucket}")
    private String bucket;

    public List<String> getAllBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        return response.buckets().stream().map(Bucket::name).toList();
    }

    public void uploadFiles(String directory, List<File> files) {
        files.forEach(file -> uploadFile(directory, file));
    }

    private void uploadFile(String directory, File file) {
        String fileExtension = "." + file.toPath().getFileName().toString().split("\\.")[1];
        String path = directory + UUID.randomUUID() + fileExtension;
        PutObjectRequest putObject = PutObjectRequest.builder().bucket(bucket).key(path).build();
        RequestBody requestBody = RequestBody.fromFile(file);
        s3Client.putObject(putObject, requestBody);
    }

    public List<String> getFilesUrls(String directory) {
        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucket).prefix(directory).build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return response.contents()
            .stream()
            .filter(s3Object -> !s3Object.key().equals(directory)) // exclude the folder marker
            .map(s3Object -> getFileUrl(s3Object.key()))
            .toList();
    }

    private String getFileUrl(String key) {
        GetUrlRequest request = GetUrlRequest.builder().bucket(bucket).key(key).build();
        String fileUrl = s3Client.utilities().getUrl(request).toString();
        return utilities.resolveS3Url(fileUrl);
    }

    public void deleteFiles(String directory) {
        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucket).prefix(directory).build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        List<S3Object> contents = response.contents();

        if (contents.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> objectsToDelete = contents
            .stream()
            .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
            .toList();

        DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
            .bucket(bucket)
            .delete(Delete.builder().objects(objectsToDelete).build())
            .build();

        s3Client.deleteObjects(deleteRequest);
    }
}
