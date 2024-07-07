package com.myapp.guess_who.room;

import com.github.fge.jsonpatch.JsonPatch;
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

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;

    private final Map<UUID, Integer> counters = new HashMap<>();

    @PostMapping("/room")
    public ResponseEntity<Room> createRoom(@RequestBody Player host) {
        return ResponseEntity.ok(roomManager.createRoom(host));
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

    @MessageMapping("/room/{roomId}/counter")
    @SendTo("/topic/room/{roomId}/counter")
    public int sendMessage(@DestinationVariable("roomId") UUID roomId, int count) {
        int newCount = counters.containsKey(roomId) ? counters.get(roomId) + count : count;
        counters.put(roomId, newCount);
        return newCount;
    }
}
