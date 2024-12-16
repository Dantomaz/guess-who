package com.myapp.guess_who.session;

import com.myapp.guess_who.room.RoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

    private final RedisSessionService redisSessionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String httpSessionId = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("SPRING.SESSION.ID").toString();
        Session httpSession = redisSessionService.getSessionById(httpSessionId);

        if (httpSession == null) {
            return;
        }

        UUID roomId = httpSession.getAttribute("roomId");
        UUID playerId = httpSession.getAttribute("playerId");

        if (roomId == null || playerId == null || !roomManager.roomExists(roomId)) {
            return;
        }

        roomManager.changePlayerConnectedStatus(roomId, playerId, true);
        messagingTemplate.convertAndSend("/topic/room/%s/players".formatted(roomId), roomManager.getRoom(roomId).getPlayers());

        log.info("room {} - {} established WebSocket connection", roomId, roomManager.getRoom(roomId).getPlayer(playerId));
    }

    @EventListener
    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String httpSessionId = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("SPRING.SESSION.ID").toString();
        Session httpSession = redisSessionService.getSessionById(httpSessionId);

        if (httpSession == null) {
            return;
        }

        UUID roomId = httpSession.getAttribute("roomId");
        UUID playerId = httpSession.getAttribute("playerId");

        if (roomId == null || playerId == null || !roomManager.roomExists(roomId)) {
            return;
        }

        roomManager.changePlayerConnectedStatus(roomId, playerId, false);
        messagingTemplate.convertAndSend("/topic/room/%s/players".formatted(roomId), roomManager.getRoom(roomId).getPlayers());

        log.info("room {} - {} lost WebSocket connection", roomId, roomManager.getRoom(roomId).getPlayer(playerId));
    }
}
