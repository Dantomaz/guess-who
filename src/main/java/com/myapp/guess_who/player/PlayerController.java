package com.myapp.guess_who.player;

import com.github.fge.jsonpatch.JsonPatch;
import com.myapp.guess_who.room.Room;
import com.myapp.guess_who.room.RoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class PlayerController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;

    @PostMapping("/player/{playerName}")
    public ResponseEntity<Player> createPlayer(@PathVariable("playerName") String playerName) {
        return ResponseEntity.ok(Player.createPlayer(playerName));
    }

    @PostMapping("/room/{roomId}/player")
    public ResponseEntity<Room> joinRoom(@PathVariable("roomId") UUID roomId, @RequestBody Player player) {
        roomManager.addPlayer(roomId, player);
        Room room = roomManager.getRoom(roomId);
        messagingTemplate.convertAndSend("/topic/room/%s".formatted(roomId), room);
        return ResponseEntity.ok(room);
    }

    @PatchMapping("/room/{roomId}/player/{playerId}")
    public ResponseEntity<Void> updatePlayer(
        @PathVariable("roomId") UUID roomId,
        @PathVariable("playerId") UUID playerId,
        @RequestBody JsonPatch jsonPatch
    ) {
        roomManager.updatePlayer(roomId, playerId, jsonPatch);
        Room room = roomManager.getRoom(roomId);
        messagingTemplate.convertAndSend("/topic/room/%s".formatted(roomId), room);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/room/{roomId}/player/{playerId}")
    public ResponseEntity<Room> leaveRoom(@PathVariable("roomId") UUID roomId, @PathVariable("playerId") UUID playerId) {
        roomManager.removePlayer(roomId, playerId);
        Room room = roomManager.getRoom(roomId);
        if (room != null) {
            messagingTemplate.convertAndSend("/topic/room/%s".formatted(roomId), room);
        }
        return ResponseEntity.ok(room);
    }
}
