package com.myapp.guess_who.room;

import com.github.fge.jsonpatch.JsonPatch;
import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.room.response.ReconnectResponse;
import com.myapp.guess_who.utils.FileMappingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RoomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;
    private final RoomService roomService;
    private final FileMappingService fileMappingService;

    @PostMapping("/room")
    public ResponseEntity<Room> createRoom(@RequestBody Player host, HttpSession httpSession) {
        Room room = roomManager.createRoom(host);
        httpSession.setAttribute("roomId", room.getId());
        return ResponseEntity.ok(room);
    }

    @PostMapping("/room/{roomId}/player")
    public ResponseEntity<Room> joinRoom(@PathVariable("roomId") UUID roomId, @RequestBody Player player, HttpSession httpSession) {
        roomManager.addPlayer(roomId, player);
        Room room = roomManager.getRoom(roomId);
        httpSession.setAttribute("roomId", room.getId());
        messagingTemplate.convertAndSend("/topic/room/%s/players".formatted(roomId), room.getPlayers());
        return ResponseEntity.ok(room);
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

    @DeleteMapping("/room/{roomId}/player/{playerId}")
    public ResponseEntity<Void> leaveRoom(@PathVariable("roomId") UUID roomId, @PathVariable("playerId") UUID playerId, HttpSession httpSession) {
        roomManager.removePlayer(roomId, playerId);
        Room room = roomManager.getRoom(roomId);
        if (room != null) {
            messagingTemplate.convertAndSend("/topic/room/%s/players".formatted(roomId), room.getPlayers());
        }
        httpSession.invalidate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/reconnect")
    public ResponseEntity<ReconnectResponse> reconnect(HttpSession httpSession) {
        if (httpSession.getAttribute("roomId") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Room room = roomManager.getRoom((UUID) httpSession.getAttribute("roomId"));
        Player player = room.getPlayer((UUID) httpSession.getAttribute("playerId"));
        return ResponseEntity.ok(new ReconnectResponse(player, room));
    }

    @PostMapping("room/{roomId}/images")
    public ResponseEntity<HashMap<Integer, String>> uploadImages(
        @PathVariable("roomId") UUID roomId,
        @RequestParam("images") List<MultipartFile> images
    ) {
        Room room = roomManager.getRoom(roomId);
        HashMap<Integer, String> uploadedImages = fileMappingService.toBase64(roomService.uploadImages(room, images));
        messagingTemplate.convertAndSend("/topic/room/%s/images".formatted(roomId), uploadedImages);
        return ResponseEntity.ok(uploadedImages);
    }
}
