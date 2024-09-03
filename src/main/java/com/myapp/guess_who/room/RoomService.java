package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameStateService;
import com.myapp.guess_who.utils.MultipartParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoomService {

    private final MultipartParsingService multipartParsingService;
    private final GameStateService gameStateService;

    public void uploadImages(Room room, List<MultipartFile> images) {
        room.setImages(multipartParsingService.toBytes(images));
        gameStateService.initializeCards(room.getGameState(), images.size());
    }
}
