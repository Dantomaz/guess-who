package com.myapp.guess_who.player;

import com.myapp.guess_who.gameState.GameState;
import com.myapp.guess_who.gameState.GameStateDTO;
import com.myapp.guess_who.room.Room;
import com.myapp.guess_who.room.RoomManager;
import com.myapp.guess_who.team.Team;
import com.myapp.guess_who.utils.StringPayload;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class PlayerController {

    private final RoomManager roomManager;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/player/{playerName}")
    public ResponseEntity<Player> createPlayer(@PathVariable("playerName") String playerName, HttpSession httpSession) {
        Player player = Player.createPlayer(playerName);
        httpSession.setAttribute("playerId", player.getId());
        return ResponseEntity.ok(player);
    }

    @MessageMapping("/room/{roomId}/player/{playerId}/changeName")
    @SendTo("/topic/room/{roomId}/players")
    public Map<UUID, Player> changePlayerName(
        @DestinationVariable("roomId") UUID roomId,
        @DestinationVariable("playerId") UUID playerId,
        @Payload StringPayload newName
    ) {
        roomManager.changePlayerName(roomId, playerId, newName.payload());
        broadcastGameStateChangeToAllTeams(roomId);

        return roomManager.getRoom(roomId).getPlayers();
    }

    @MessageMapping("/room/{roomId}/player/{playerId}/changeTeam")
    @SendTo("/topic/room/{roomId}/players")
    public Map<UUID, Player> changePlayerTeam(
        @DestinationVariable("roomId") UUID roomId,
        @DestinationVariable("playerId") UUID playerId,
        @Payload Team newTeam
    ) {
        roomManager.changePlayerTeam(roomId, playerId, newTeam);
        return roomManager.getRoom(roomId).getPlayers();
    }

    @MessageMapping("/room/{roomId}/resetTeams")
    @SendTo("/topic/room/{roomId}/players")
    public Map<UUID, Player> resetTeams(@DestinationVariable("roomId") UUID roomId) {
        Room room = roomManager.getRoom(roomId);
        room.resetTeams();
        return room.getPlayers();
    }

    @MessageMapping("/room/{roomId}/randomizeTeams")
    @SendTo("/topic/room/{roomId}/players")
    public Map<UUID, Player> randomizeTeams(@DestinationVariable("roomId") UUID roomId) {
        Room room = roomManager.getRoom(roomId);
        room.randomizeTeams();
        return room.getPlayers();
    }

    @MessageMapping("/room/{roomId}/player/{playerId}/makeHost")
    @SendTo("/topic/room/{roomId}/players")
    public Map<UUID, Player> makePlayerHost(@DestinationVariable("roomId") UUID roomId, @DestinationVariable("playerId") UUID playerId) {
        roomManager.makePlayerHost(roomId, playerId);
        return roomManager.getRoom(roomId).getPlayers();
    }

    @MessageMapping("/room/{roomId}/player/{playerId}/kick")
    @SendTo("/topic/room/{roomId}/players")
    public Map<UUID, Player> kickPlayer(@DestinationVariable("roomId") UUID roomId, @DestinationVariable("playerId") UUID playerId) {
        roomManager.removePlayer(roomId, playerId);
        messagingTemplate.convertAndSend("/topic/room/%s/player/%s/disconnect".formatted(roomId, playerId), "kick");

        Player host = roomManager.getRoom(roomId).getHost();
        log.info("room {} - {} kicked {}", roomId, host, roomManager.getRoom(roomId).getPlayer(playerId));

        return roomManager.getRoom(roomId).getPlayers();
    }

    private void broadcastGameStateChangeToAllTeams(UUID roomId) {
        GameState gameState = roomManager.getRoom(roomId).getGameState();

        String destination = "/topic/room/%s/gameState/team/%s";
        messagingTemplate.convertAndSend(destination.formatted(roomId, Team.BLUE), new GameStateDTO(gameState, Team.BLUE));
        messagingTemplate.convertAndSend(destination.formatted(roomId, Team.RED), new GameStateDTO(gameState, Team.RED));
    }
}
