package com.myapp.guess_who.gameState;

import com.myapp.guess_who.gameState.request.CardCloseRequest;
import com.myapp.guess_who.gameState.request.VoteRequest;
import com.myapp.guess_who.room.Room;
import com.myapp.guess_who.room.RoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class GameStateController {

    private final RoomManager roomManager;
    private final GameStateService gameStateService;

    @MessageMapping("/room/{roomId}/restartGame")
    @SendTo("/topic/room/{roomId}/gameState")
    public GameState restartGame(@DestinationVariable("roomId") UUID roomId) {
        GameState gameState = roomManager.getRoom(roomId).getGameState();
        gameStateService.resetGameState(gameState);
        return gameState;
    }

    @MessageMapping("/room/{roomId}/prepareGame")
    @SendTo("/topic/room/{roomId}/gameState")
    public GameState prepareGame(@DestinationVariable("roomId") UUID roomId) {
        GameState gameState = roomManager.getRoom(roomId).getGameState();
        gameStateService.prepareGame(gameState);
        return gameState;
    }

    @MessageMapping("/room/{roomId}/vote")
    @SendTo("/topic/room/{roomId}/gameState")
    public GameState voteForCard(@DestinationVariable("roomId") UUID roomId, @RequestBody VoteRequest voteRequest) {
        Room room = roomManager.getRoom(roomId);
        GameState gameState = room.getGameState();
        gameStateService.voteForCard(gameState, room.getPlayer(voteRequest.playerId()), voteRequest.cardNumber());
        return gameState;
    }

    @MessageMapping("/room/{roomId}/startGame")
    @SendTo("/topic/room/{roomId}/gameState")
    public GameState startGame(@DestinationVariable("roomId") UUID roomId) {
        GameState gameState = roomManager.getRoom(roomId).getGameState();
        gameStateService.startGame(gameState);
        return gameState;
    }

    @MessageMapping("/room/{roomId}/toggleCard")
    @SendTo("/topic/room/{roomId}/gameState")
    public GameState toggleCard(@DestinationVariable("roomId") UUID roomId, @RequestBody CardCloseRequest cardCloseRequest) {
        GameState gameState = roomManager.getRoom(roomId).getGameState();
        gameStateService.toggleCard(gameState, cardCloseRequest.cardNumber(), cardCloseRequest.team());
        return gameState;
    }

    @MessageMapping("/room/{roomId}/endTurn")
    @SendTo("/topic/room/{roomId}/gameState")
    public GameState endTurn(@DestinationVariable("roomId") UUID roomId) {
        GameState gameState = roomManager.getRoom(roomId).getGameState();
        gameStateService.nextTurn(gameState);
        return gameState;
    }
}
