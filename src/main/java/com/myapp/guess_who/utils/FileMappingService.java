package com.myapp.guess_who.utils;

import com.myapp.guess_who.exception.customException.NotEnoughImagesException;
import com.myapp.guess_who.exception.customException.TooManyImagesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class FileMappingService {

    private static final int IMAGES_MIN_COUNT = 12;
    private static final int IMAGES_MAX_COUNT = 24;
    // %s is a placeholder for roomId, easy to insert via String instance .formatted() method
    public static final String ROOM_DIR = Path.of(System.getProperty("user.dir") + "/public/room_%s").normalize().toString();
    public static final String CUSTOM_IMAGES_UPLOAD_DIR = ROOM_DIR + "/images/";
    private final String CUSTOM_IMAGES_URL;
    private final String DEFAULT_IMAGES_URL;

    public FileMappingService(@Value("${custom.application.url}") String applicationUrl) {
        CUSTOM_IMAGES_URL = applicationUrl + "/room_%s/images/";
        DEFAULT_IMAGES_URL = applicationUrl + "/defaultimages/";
    }

    public HashMap<Integer, String> storeImages(UUID roomId, List<MultipartFile> images) {
        int numberOfImages = images.size();

        if (numberOfImages < IMAGES_MIN_COUNT) {
            throw new NotEnoughImagesException("Not enough images (%s uploaded, but min is %s)".formatted(numberOfImages, IMAGES_MIN_COUNT));
        }

        if (numberOfImages > IMAGES_MAX_COUNT) {
            throw new TooManyImagesException("Too many images (%s uploaded, but max is %s)".formatted(numberOfImages, IMAGES_MAX_COUNT));
        }

        HashMap<Integer, String> uploadedImageUrls = new HashMap<>();

        cleanUpImages(roomId);
        createDirectories(roomId);

        for (int i = 0; i < numberOfImages; i++) {
            MultipartFile image = images.get(i);
            try {
                // Generate a unique filename for each image
                String uniqueImageName = UUID.randomUUID().toString();

                // Construct the directory path where the image will be stored
                String imageDir = CUSTOM_IMAGES_UPLOAD_DIR.formatted(roomId) + uniqueImageName;

                // Store the image in the specified directory
                File destinationFile = new File(imageDir);
                image.transferTo(destinationFile);
                log.info("Image saved to: {}", destinationFile.getCanonicalPath());

                // Construct the URL where the image will be accessible
                String imageUrl = CUSTOM_IMAGES_URL.formatted(roomId) + uniqueImageName;

                // Store the image URL in the HashMap with its index
                uploadedImageUrls.put(i, imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image: %s.%s%s".formatted(
                    image.getOriginalFilename(),
                    System.lineSeparator(),
                    e.getMessage()
                ));
            }
        }

        return uploadedImageUrls;
    }

    private void createDirectories(UUID roomId) {
        // Create a directory for the specific room if it doesn't exist yet
        try {
            Path path = Path.of(CUSTOM_IMAGES_UPLOAD_DIR.formatted(roomId));
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: %s".formatted(e.getMessage()));
        }
    }

    public void cleanUpImages(UUID roomId) {
        Path path = Path.of(ROOM_DIR.formatted(roomId));

        try {
            // Delete all images
            FileSystemUtils.deleteRecursively(path);
            // Now attempt to delete the directory itself
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to clean up images in directory: %s.%sMessage:%s%s".formatted(
                path,
                System.lineSeparator(),
                System.lineSeparator(),
                e.getMessage()
            ));
        }
    }

    public HashMap<Integer, String> getDefaultImages() {
        return IntStream.rangeClosed(1, IMAGES_MAX_COUNT)
            .boxed()
            .collect(Collectors.toMap(
                number -> number,
                number -> DEFAULT_IMAGES_URL + number + ".jpg",
                (existing, replacement) -> existing,
                HashMap::new
            ));
    }
}
