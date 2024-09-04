package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameStateService;
import com.myapp.guess_who.utils.FileMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RoomService {

    private final FileMappingService fileMappingService;
    private final GameStateService gameStateService;

    public HashMap<Integer, byte[]> uploadImages(Room room, List<MultipartFile> images) {
        room.setImages(fileMappingService.toBytes(images));
        gameStateService.initializeCards(room.getGameState(), images.size());
        return room.getImages();
    }
}
