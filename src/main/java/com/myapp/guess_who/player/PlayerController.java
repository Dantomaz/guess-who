package com.myapp.guess_who.player;

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
    private final PlayerService playerService;

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
        Map<UUID, Player> players = roomManager.getRoom(roomId).getPlayers();
        playerService.changePlayerName(players, playerId, newName.payload());
        return players;
    }

    @MessageMapping("/room/{roomId}/player/{playerId}/changeTeam")
    @SendTo("/topic/room/{roomId}/players")
    public Map<UUID, Player> changePlayerTeam(
        @DestinationVariable("roomId") UUID roomId,
        @DestinationVariable("playerId") UUID playerId,
        @Payload Team newTeam
    ) {
        Map<UUID, Player> players = roomManager.getRoom(roomId).getPlayers();
        playerService.changePlayerTeam(players, playerId, newTeam);
        return players;
    }
}
