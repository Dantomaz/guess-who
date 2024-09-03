package com.myapp.guess_who.gameState;

import com.myapp.guess_who.room.RoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class GameStateController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;
    private final GameStateService gameStateService;
}
