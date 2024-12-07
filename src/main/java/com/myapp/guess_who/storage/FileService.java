package com.myapp.guess_who.storage;

import com.myapp.guess_who.api.S3Service;
import com.myapp.guess_who.exception.customException.NotEnoughImagesException;
import com.myapp.guess_who.exception.customException.TooManyImagesException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class FileService {

    private final S3Service s3Service;

    private static final int IMAGES_MIN_COUNT = 12;
    private static final int IMAGES_MAX_COUNT = 24;
    private final static String CUSTOM_IMAGES_DIRECTORY = "images/room_%s/";
    private final static String DEFAULT_IMAGES_DIRECTORY = "images/default/";

    public void uploadCustomImages(UUID roomId, List<MultipartFile> images) {
        int numberOfImages = images.size();

        if (numberOfImages < IMAGES_MIN_COUNT) {
            throw new NotEnoughImagesException("Not enough images (%s uploaded, but min is %s)".formatted(numberOfImages, IMAGES_MIN_COUNT));
        }

        if (numberOfImages > IMAGES_MAX_COUNT) {
            throw new TooManyImagesException("Too many images (%s uploaded, but max is %s)".formatted(numberOfImages, IMAGES_MAX_COUNT));
        }

        deleteCustomImages(roomId);
        String directory = CUSTOM_IMAGES_DIRECTORY.formatted(roomId);
        List<File> files = convertMultipartFilesToFiles(images);
        s3Service.uploadFiles(directory, files);
    }

    private List<File> convertMultipartFilesToFiles(List<MultipartFile> files) {
        return files.stream().map(this::convertMultipartFileToFile).toList();
    }

    private File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try (FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)) {
            fileOutputStream.write(file.getBytes());
        } catch (IOException exception) {
            throw new RuntimeException("Failed to convert file %s".formatted(file.getOriginalFilename()), exception);
        }

        return convertedFile;
    }

    public void deleteCustomImages(UUID roomId) {
        s3Service.deleteFiles(CUSTOM_IMAGES_DIRECTORY.formatted(roomId));
    }

    public Map<Integer, String> getCustomImagesUrls(UUID roomId) {
        List<String> images = s3Service.getFilesUrls(CUSTOM_IMAGES_DIRECTORY.formatted(roomId));
        return convertToMap(images);
    }

    public Map<Integer, String> getDefaultImagesUrls() {
        List<String> images = s3Service.getFilesUrls(DEFAULT_IMAGES_DIRECTORY);
        return convertToMap(images);
    }

    private Map<Integer, String> convertToMap(List<String> images) {
        return IntStream.range(0, images.size())
            .boxed()
            .collect(Collectors.toMap(
                Function.identity(),
                images::get
            ));
    }
}
