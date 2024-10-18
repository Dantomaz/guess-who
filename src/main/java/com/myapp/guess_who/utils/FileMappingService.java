package com.myapp.guess_who.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileMappingService {

    // %s is a placeholder for roomId, easy to insert via String instance .formatted() method
    public static final String ROOM_DIR = System.getProperty("user.dir") + "/public/room_%s/";
    public static final String IMAGES_UPLOAD_DIR = ROOM_DIR + "images/";
    private static final String IMAGES_URL = "http://localhost:8080/room_%s/images/";

    public HashMap<Integer, String> storeImages(UUID roomId, List<MultipartFile> images) {
        HashMap<Integer, String> uploadedImageUrls = new HashMap<>();

        cleanUpImages(roomId);
        createDirectories(roomId);

        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            try {
                // Generate a unique filename for each image
                String uniqueImageName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());

                // Construct the directory path where the image will be stored
                String imageDir = IMAGES_UPLOAD_DIR.formatted(roomId) + uniqueImageName;

                // Store the image in the specified directory
                File destinationFile = new File(imageDir);
                image.transferTo(destinationFile);

                // Construct the URL where the image will be accessible
                String imageUrl = IMAGES_URL.formatted(roomId) + uniqueImageName;

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
            Path path = Path.of(String.format(IMAGES_UPLOAD_DIR, roomId));
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: %s".formatted(e.getMessage()));
        }
    }

    public void cleanUpImages(UUID roomId) {
        Path path = Path.of(FileMappingService.ROOM_DIR.formatted(roomId));

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
}
