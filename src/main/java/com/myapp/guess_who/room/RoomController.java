package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameStateDTO;
import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.room.response.ReconnectResponse;
import com.myapp.guess_who.utils.FileMappingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RoomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;
    private final FileMappingService fileMappingService;

    @PostMapping("/room")
    public ResponseEntity<RoomDTO> createRoom(@RequestBody Player host, HttpSession httpSession) {
        Room room = roomManager.createRoom(host);
        httpSession.setAttribute("roomId", room.getId());
        return ResponseEntity.ok(new RoomDTO(room, host.getTeam()));
    }

    @PostMapping("/room/{roomId}/player")
    public ResponseEntity<RoomDTO> joinRoom(@PathVariable("roomId") UUID roomId, @RequestBody Player player, HttpSession httpSession) {
        roomManager.addPlayer(roomId, player);
        Room room = roomManager.getRoom(roomId);
        httpSession.setAttribute("roomId", room.getId());
        messagingTemplate.convertAndSend("/topic/room/%s/players".formatted(roomId), room.getPlayers());
        return ResponseEntity.ok(new RoomDTO(room, player.getTeam()));
    }

    @DeleteMapping("/room/{roomId}/player/{playerId}")
    public ResponseEntity<Void> leaveRoom(@PathVariable("roomId") UUID roomId, @PathVariable("playerId") UUID playerId) {
        Room room = roomManager.getRoom(roomId);
        roomManager.removePlayer(roomId, playerId);

        if (room == null) {
            return ResponseEntity.ok().build();
        }

        messagingTemplate.convertAndSend("/topic/room/%s/players".formatted(roomId), room.getPlayers());

        if (room.getPlayers().size() == 1) {
            // Need to send game state update to the client to stop the game - player can't play alone
            Player player = room.getPlayers().values().stream().reduce(null, (player1, player2) -> player2);
            messagingTemplate.convertAndSend(
                "/topic/room/%s/gameState/team/%s".formatted(roomId, player.getTeam()),
                new GameStateDTO(room.getGameState(), player.getTeam())
            );
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/reconnect")
    public ResponseEntity<ReconnectResponse> reconnect(HttpSession httpSession) {
        if (httpSession.getAttribute("roomId") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Room room = roomManager.getRoom((UUID) httpSession.getAttribute("roomId"));
        if (room == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Player player = room.getPlayer((UUID) httpSession.getAttribute("playerId"));
        return ResponseEntity.ok(new ReconnectResponse(player, new RoomDTO(room, player.getTeam())));
    }

    @PostMapping("room/{roomId}/images")
    public ResponseEntity<HashMap<Integer, String>> uploadImages(
        @PathVariable("roomId") UUID roomId,
        @RequestParam("images") List<MultipartFile> images
    ) {
        Room room = roomManager.getRoom(roomId);
        HashMap<Integer, String> uploadedImages = fileMappingService.storeImages(roomId, images);
        room.setImages(uploadedImages);
        messagingTemplate.convertAndSend("/topic/room/%s/images".formatted(roomId), uploadedImages);
        return ResponseEntity.ok(uploadedImages);
    }
}
