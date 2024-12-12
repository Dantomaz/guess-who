package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.room.response.ReconnectResponse;
import com.myapp.guess_who.storage.FileService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RoomController {

    @Value("${spring.session.timeout}")
    private int sessionTimeoutInSeconds;

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;
    private final FileService fileService;

    @PostMapping("/room")
    public ResponseEntity<RoomDTO> createRoom(@RequestBody Player host, HttpSession httpSession, HttpServletResponse response) {
        Room room = roomManager.createRoom(host);

        if (httpSession.getAttribute("playerId") == null) {
            httpSession.setAttribute("playerId", host.getId());
        }
        httpSession.setAttribute("roomId", room.getId());
        response.addCookie(createReconnectCookie());

        return ResponseEntity.ok(new RoomDTO(room, host.getTeam()));
    }

    private Cookie createReconnectCookie() {
        Cookie cookie = new Cookie("RECONNECT", "true");
        cookie.setMaxAge(sessionTimeoutInSeconds);
        cookie.setHttpOnly(false);
        cookie.setSecure(true);
        cookie.setPath("/");
        return cookie;
    }

    @PostMapping("/room/{roomId}/player")
    public ResponseEntity<RoomDTO> joinRoom(
        @PathVariable("roomId") UUID roomId,
        @RequestBody Player player,
        HttpSession httpSession,
        HttpServletResponse response
    ) {
        roomManager.addPlayer(roomId, player);
        roomManager.verifyRoomHasViableHost(roomId);
        Room room = roomManager.getRoom(roomId);

        if (httpSession.getAttribute("playerId") == null) {
            httpSession.setAttribute("playerId", player.getId());
        }
        httpSession.setAttribute("roomId", room.getId());
        response.addCookie(createReconnectCookie());

        messagingTemplate.convertAndSend("/topic/room/%s/players".formatted(roomId), room.getPlayers());
        return ResponseEntity.ok(new RoomDTO(room, player.getTeam()));
    }

    @DeleteMapping("/room/{roomId}/player/{playerId}")
    public ResponseEntity<Void> leaveRoom(@PathVariable("roomId") UUID roomId, @PathVariable("playerId") UUID playerId) {
        Room room = roomManager.getRoom(roomId);
        roomManager.removePlayer(roomId, playerId);

        messagingTemplate.convertAndSend("/topic/room/%s/players".formatted(roomId), room.getPlayers());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/reconnect")
    public ResponseEntity<ReconnectResponse> reconnect(HttpSession httpSession, HttpServletResponse response) {
        UUID roomId = (UUID) httpSession.getAttribute("roomId");
        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Room room = roomManager.getRoom(roomId);
        if (room == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Player player = room.getPlayer((UUID) httpSession.getAttribute("playerId"));
        player.setConnected(true);
        response.addCookie(createReconnectCookie());
        roomManager.verifyRoomHasViableHost(roomId);

        return ResponseEntity.ok(new ReconnectResponse(player, new RoomDTO(room, player.getTeam())));
    }

    @PostMapping("/room/{roomId}/images")
    public ResponseEntity<Map<Integer, String>> uploadImages(
        @PathVariable("roomId") UUID roomId,
        @RequestParam("images") List<MultipartFile> images
    ) {
        Room room = roomManager.getRoom(roomId);
        fileService.uploadCustomImages(roomId, images);
        Map<Integer, String> uploadedImages = fileService.getCustomImagesUrls(roomId);
        room.setImages(uploadedImages);

        messagingTemplate.convertAndSend("/topic/room/%s/images".formatted(roomId), uploadedImages);
        return ResponseEntity.ok(uploadedImages);
    }
}
