package com.myapp.guess_who.session;

import com.myapp.guess_who.room.RoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.session.Session;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HttpSessionEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;

    @EventListener
    public void handleSessionDestroyedEvent(SessionDestroyedEvent event) {
        Session session = event.getSession();
        UUID roomId = session.getAttribute("roomId");
        UUID playerId = session.getAttribute("playerId");

        if (roomId == null || playerId == null || !roomManager.roomExists(roomId)) {
            return;
        }

        roomManager.removePlayer(roomId, playerId);
        messagingTemplate.convertAndSend("/topic/room/%s/player/%s/disconnect".formatted(roomId, playerId), "timeout");
    }
}
