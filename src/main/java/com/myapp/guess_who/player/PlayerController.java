package com.myapp.guess_who.player;

import com.myapp.guess_who.player.request.NewNameRequest;
import com.myapp.guess_who.room.RoomManager;
import com.myapp.guess_who.team.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class PlayerController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;
    private final PlayerService playerService;

    @PostMapping("/player/{playerName}")
    public ResponseEntity<Player> createPlayer(@PathVariable("playerName") String playerName) {
        return ResponseEntity.ok(Player.createPlayer(playerName));
    }

    @MessageMapping("/room/{roomId}/player/{playerId}/changeName")
    @SendTo("/topic/room/{roomId}/players")
    public Map<UUID, Player> changePlayerName(
        @DestinationVariable("roomId") UUID roomId,
        @DestinationVariable("playerId") UUID playerId,
        @RequestBody NewNameRequest newNameRequest
    ) {
        Map<UUID, Player> players = roomManager.getRoom(roomId).getPlayers();
        playerService.changePlayerName(players, playerId, newNameRequest.newName());
        return players;
    }

    @MessageMapping("/room/{roomId}/player/{playerId}/changeTeam")
    @SendTo("/topic/room/{roomId}/players")
    public Map<UUID, Player> changePlayerTeam(
        @DestinationVariable("roomId") UUID roomId,
        @DestinationVariable("playerId") UUID playerId,
        @RequestBody Team newTeam
    ) {
        Map<UUID, Player> players = roomManager.getRoom(roomId).getPlayers();
        playerService.changePlayerTeam(players, playerId, newTeam);
        return players;
    }
}
