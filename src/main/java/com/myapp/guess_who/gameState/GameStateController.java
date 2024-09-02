package com.myapp.guess_who.gameState;

import com.myapp.guess_who.room.Room;
import com.myapp.guess_who.room.RoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
public class GameStateController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;
    private final GameStateService gameStateService;

    @PostMapping("room/{roomId}/images")
    public ResponseEntity<Void> uploadImages(@PathVariable("roomId") UUID roomId, @RequestParam("images") List<MultipartFile> images) {
        Room room = roomManager.getRoom(roomId);
        gameStateService.uploadImages(room.getGameState(), images);
        return ResponseEntity.ok().build();
    }

    @GetMapping("room/{roomId}/images")
    public ResponseEntity<HashMap<Integer, String>> downloadImages(@PathVariable("roomId") UUID roomId) {
        HashMap<Integer, String> images = roomManager.getRoom(roomId).getGameState().getImages().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Base64.getEncoder().encodeToString(entry.getValue()),
                (map1, map2) -> map1,
                HashMap::new
            ));
        return ResponseEntity.ok(images);
    }
}
