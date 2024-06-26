package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RoomController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<UUID, Integer> counters = new HashMap<>();

    @PostMapping("/room/create")
    public ResponseEntity<Room> createRoom(@RequestBody Player host) {
        return ResponseEntity.ok(roomService.createRoom(host));
    }

    @PatchMapping("/room/{id}/join")
    public ResponseEntity<Room> joinRoom(@PathVariable("id") UUID roomId, @RequestBody Player player) {
        Room room = roomService.joinRoom(player, roomId);
        messagingTemplate.convertAndSend("/topic/room/%s".formatted(roomId), room);
        return ResponseEntity.ok(room);
    }

    @PatchMapping("/room/{id}/leave")
    public ResponseEntity<Room> leaveRoom(@PathVariable("id") UUID roomId, @RequestBody Player player) {
        Room room = roomService.leaveRoom(player, roomId);
        if (room != null) {
            messagingTemplate.convertAndSend("/topic/room/%s".formatted(roomId), room);
        }
        return ResponseEntity.ok(room);
    }

    @MessageMapping("/room/{id}/counter")
    @SendTo("/topic/room/{id}/counter")
    public int sendMessage(@DestinationVariable("id") UUID roomId, int count) {
        int newCount = counters.containsKey(roomId) ? counters.get(roomId) + count : count;
        counters.put(roomId, newCount);
        return newCount;
    }
}
