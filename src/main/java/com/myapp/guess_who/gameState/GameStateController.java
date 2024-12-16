package com.myapp.guess_who.gameState;

import com.myapp.guess_who.gameState.request.ToggleCardRequest;
import com.myapp.guess_who.gameState.request.VoteRequest;
import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.room.Room;
import com.myapp.guess_who.room.RoomManager;
import com.myapp.guess_who.team.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class GameStateController {

    private final RoomManager roomManager;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room/{roomId}/restartGame")
    public void restartGame(@DestinationVariable("roomId") UUID roomId) {
        Room room = roomManager.getRoom(roomId);
        GameState gameState = room.getGameState();
        gameState.resetGame();
        room.clearImages();

        messagingTemplate.convertAndSend("/topic/room/%s/images".formatted(roomId), room.getImages());
        broadcastGameStateChangeToAllTeams(roomId, gameState);
    }

    @MessageMapping("/room/{roomId}/prepareGame")
    public void prepareGame(@DestinationVariable("roomId") UUID roomId, @Payload Boolean useDefaultImages) {
        Room room = roomManager.getRoom(roomId);
        GameState gameState = room.getGameState();

        if (useDefaultImages) {
            room.setImages(roomManager.getDefaultImages());
            messagingTemplate.convertAndSend("/topic/room/%s/images".formatted(roomId), room.getImages());
        }

        gameState.prepareGame(room.getImages().size());

        broadcastGameStateChangeToAllTeams(roomId, gameState);
    }

    @MessageMapping("/room/{roomId}/vote")
    public void voteForCard(
        @DestinationVariable("roomId") UUID roomId,
        @Payload VoteRequest voteRequest
    ) {
        Room room = roomManager.getRoom(roomId);
        GameState gameState = room.getGameState();
        Player player = room.getPlayer(voteRequest.playerId());
        Team team = player.getTeam();
        Integer cardNumber = voteRequest.cardNumber();

        gameState.addPlayerVote(team, player.getId(), cardNumber);

        if (room.getPlayers().size() == gameState.getTotalNumberOfPlayersVotes()) {
            gameState.startGame();
        }

        broadcastGameStateChangeToAllTeams(roomId, gameState);

        log.debug("room {} - {} voted for card number {}", roomId, player, cardNumber);
    }

    @MessageMapping("/room/{roomId}/toggleCard")
    public void toggleCard(
        @DestinationVariable("roomId") UUID roomId,
        @Payload ToggleCardRequest toggleCardRequest
    ) {
        GameState gameState = roomManager.getRoom(roomId).getGameState();
        Integer cardNumber = toggleCardRequest.cardNumber();
        Team team = toggleCardRequest.team();
        gameState.toggleCardByPlayer(team, cardNumber);

        broadcastGameStateChangeToTeam(roomId, gameState, team);

        log.debug("room {} - team {} toggled card number {}", roomId, team.toString(), cardNumber);
    }

    @MessageMapping("/room/{roomId}/player/{playerId}/endTurn")
    public void endTurn(@DestinationVariable("roomId") UUID roomId, @DestinationVariable("playerId") UUID playerId) {
        Room room = roomManager.getRoom(roomId);
        GameState gameState = room.getGameState();
        Player player = room.getPlayer(playerId);
        gameState.endCurrentTurn();
        gameState.saveActivity(player);

        broadcastGameStateChangeToAllTeams(roomId, gameState);

        log.debug("room {} - {} ended a turn", roomId, player);
    }

    @MessageMapping("/room/{roomId}/player/{playerId}/guessCard")
    public void guessCard(@DestinationVariable("roomId") UUID roomId, @DestinationVariable("playerId") UUID playerId, int cardNumber) {
        Room room = roomManager.getRoom(roomId);
        GameState gameState = room.getGameState();
        Player player = room.getPlayer(playerId);
        gameState.guessCard(cardNumber);
        gameState.saveActivity(player, cardNumber);

        broadcastGameStateChangeToAllTeams(roomId, gameState);

        log.debug("room {} - {} ended a turn", roomId, player);
    }

    private void broadcastGameStateChangeToTeam(UUID roomId, GameState gameState, Team team) {
        messagingTemplate.convertAndSend("/topic/room/%s/gameState/team/%s".formatted(roomId, team), new GameStateDTO(gameState, team));
    }

    private void broadcastGameStateChangeToAllTeams(UUID roomId, GameState gameState) {
        String destination = "/topic/room/%s/gameState/team/%s";
        messagingTemplate.convertAndSend(destination.formatted(roomId, Team.BLUE), new GameStateDTO(gameState, Team.BLUE));
        messagingTemplate.convertAndSend(destination.formatted(roomId, Team.RED), new GameStateDTO(gameState, Team.RED));
    }
}
