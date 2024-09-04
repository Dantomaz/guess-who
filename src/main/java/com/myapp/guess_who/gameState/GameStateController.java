package com.myapp.guess_who.gameState;

import com.myapp.guess_who.room.RoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class GameStateController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;
    private final GameStateService gameStateService;

    @MessageMapping("/room/{roomId}/prepareGame")
    @SendTo("/topic/room/{roomId}/gameState")
    public GameState prepareGame(@DestinationVariable("roomId") UUID roomId) {
        GameState gameState = roomManager.getRoom(roomId).getGameState();
        gameState.setStatus(GameState.Status.STARTING);
        return gameState;
    }
}
