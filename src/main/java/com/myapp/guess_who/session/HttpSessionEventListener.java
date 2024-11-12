package com.myapp.guess_who.session;

import com.myapp.guess_who.room.RoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.session.Session;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HttpSessionEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;

    @EventListener
    public void handleSessionCreatedEvent(SessionCreatedEvent event) {
        System.out.printf("Session %s created%n", event.getSession().getId());
        System.out.printf("%s - creation time%n%n", event.getSession().getCreationTime().atZone(ZoneId.systemDefault()).toLocalTime());
    }

    @EventListener
    public void handleSessionDestroyedEvent(SessionDestroyedEvent event) {
        System.out.printf("Session %s destroyed%n", event.getSession().getId());
        System.out.printf("%s - creation time%n", event.getSession().getCreationTime().atZone(ZoneId.systemDefault()).toLocalTime());
        System.out.printf("%s - last accessed time%n", event.getSession().getLastAccessedTime().atZone(ZoneId.systemDefault()).toLocalTime());
        System.out.printf("%s - current time%n%n", Instant.now().truncatedTo(ChronoUnit.MILLIS).atZone(ZoneId.systemDefault()).toLocalTime());

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
