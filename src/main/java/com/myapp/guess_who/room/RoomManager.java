package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.storage.FileService;
import com.myapp.guess_who.team.Team;
import com.myapp.guess_who.validator.PlayerValidator;
import com.myapp.guess_who.validator.RoomValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomManager {

    private final Map<UUID, Room> rooms = new HashMap<>();
    private final Map<Integer, String> defaultImages = new HashMap<>();
    private final RoomValidator roomValidator;
    private final PlayerValidator playerValidator;
    private final FileService fileService;

    @PostConstruct
    private void initDefaultImages() {
        defaultImages.putAll(fileService.getDefaultImagesUrls());
    }

    public Room createRoom(Player host) {
        playerValidator.validatePlayer(host);

        Room newRoom = Room.create();

        host.setHost(true);
        newRoom.addPlayer(host);

        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
    }

    public Room getRoom(UUID roomId) {
        return rooms.get(roomId);
    }

    public boolean roomExists(UUID roomId) {
        return rooms.containsKey(roomId);
    }

    public void closeRoom(UUID roomId) {
        rooms.remove(roomId);
        fileService.deleteCustomImages(roomId);
        log.info("room {} - room was closed", roomId);
    }

    public void addPlayer(UUID roomId, Player player) {
        playerValidator.validatePlayer(player);
        roomValidator.validateRoom(roomId, rooms);

        Room room = rooms.get(roomId);
        room.addPlayer(player);
    }

    public void removePlayer(UUID roomId, UUID playerId) {
        playerValidator.validatePlayerId(playerId);
        roomValidator.validateRoomId(roomId, rooms);

        Room room = rooms.get(roomId);
        room.removePlayer(playerId);

        // Room can be closed if there are no players left
        if (room.isEmpty()) {
            closeRoom(roomId);
        } else {
            verifyRoomHasViableHost(roomId);
        }
    }

    public void changePlayerConnectedStatus(UUID roomId, UUID playerId, boolean connectedStatus) {
        playerValidator.validatePlayerId(playerId);
        roomValidator.validateRoomId(roomId, rooms);

        Room room = rooms.get(roomId);

        if (!room.hasPlayer(playerId)) {
            return;
        }

        room.changePlayerConnectedStatus(playerId, connectedStatus);
        if (room.getPlayer(playerId).isHost()) {
            room.selectNewHostAtRandom();
        }
    }

    public void changePlayerName(UUID roomId, UUID playerId, String newName) {
        Room room = rooms.get(roomId);
        Map<UUID, Player> players = room.getPlayers();
        players.get(playerId).setName(newName);
        room.getGameState().updatePlayerNameInActivities(playerId, newName);
    }

    public void changePlayerTeam(UUID roomId, UUID playerId, Team newTeam) {
        Map<UUID, Player> players = rooms.get(roomId).getPlayers();
        players.get(playerId).setTeam(newTeam);
    }

    public Map<Integer, String> getDefaultImages() {
        return new HashMap<>(defaultImages);
    }

    public void makePlayerHost(UUID roomId, UUID playerId) {
        Room room = rooms.get(roomId);
        room.switchHostTo(playerId);
    }

    public void verifyRoomHasViableHost(UUID roomId) {
        Room room = rooms.get(roomId);
        if (!room.hasViableHost()) {
            room.selectNewHostAtRandom();
        }
    }
}
