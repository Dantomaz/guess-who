package com.myapp.guess_who.room;

import com.github.fge.jsonpatch.JsonPatch;
import com.myapp.guess_who.gameState.GameStateService;
import com.myapp.guess_who.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class RoomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;
    private final RoomService roomService;
    private final GameStateService gameStateService;

    @PostMapping("/room")
    public ResponseEntity<Room> createRoom(@RequestBody Player host) {
        return ResponseEntity.ok(roomManager.createRoom(host, gameStateService.getNewGameState()));
    }

    @PatchMapping("/room/{roomId}")
    public ResponseEntity<Void> updateRoom(
        @PathVariable("roomId") UUID roomId,
        @RequestBody JsonPatch jsonPatch
    ) {
        roomManager.updateRoom(roomId, jsonPatch);
        Room room = roomManager.getRoom(roomId);
        messagingTemplate.convertAndSend("/topic/room/%s".formatted(roomId), room);
        return ResponseEntity.ok().build();
    }

    @PostMapping("room/{roomId}/images")
    public ResponseEntity<Void> uploadImages(@PathVariable("roomId") UUID roomId, @RequestParam("images") List<MultipartFile> images) {
        Room room = roomManager.getRoom(roomId);
        roomService.uploadImages(room, images);
        return ResponseEntity.ok().build();
    }

    @GetMapping("room/{roomId}/images")
    public ResponseEntity<HashMap<Integer, String>> downloadImages(@PathVariable("roomId") UUID roomId) {
        HashMap<Integer, String> images = roomManager.getRoom(roomId).getImages().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Base64.getEncoder().encodeToString(entry.getValue()),
                (map1, map2) -> map1,
                HashMap::new
            ));
        return ResponseEntity.ok(images);
    }
}
