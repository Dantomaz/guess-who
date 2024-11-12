package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.team.Team;
import com.myapp.guess_who.utils.FileMappingService;
import com.myapp.guess_who.validator.PlayerValidator;
import com.myapp.guess_who.validator.RoomValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomManager {

    private final Map<UUID, Room> rooms = new HashMap<>();
    private final HashMap<Integer, String> defaultImages = new HashMap<>();
    private final RoomValidator roomValidator;
    private final PlayerValidator playerValidator;
    private final FileMappingService fileMappingService;

    @PostConstruct
    private void initDefaultImages() {
        defaultImages.putAll(fileMappingService.getDefaultImages());
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
            fileMappingService.cleanUpImages(roomId);
            return;
        }

        chooseNewHostAtRandom(room.getPlayers());
    }

    private void chooseNewHostAtRandom(Map<UUID, Player> players) {
        UUID randomId = chooseRandomPlayerId(players);
        players.get(randomId).setHost(true);
    }

    private UUID chooseRandomPlayerId(Map<UUID, Player> players) {
        int randomIndex = new Random().nextInt(players.size());
        return players.keySet().stream().toList().get(randomIndex);
    }

    public void changePlayerName(UUID roomId, UUID playerId, String newName) {
        Map<UUID, Player> players = rooms.get(roomId).getPlayers();
        players.get(playerId).setName(newName);
    }

    public void changePlayerTeam(UUID roomId, UUID playerId, Team newTeam) {
        Map<UUID, Player> players = rooms.get(roomId).getPlayers();
        players.get(playerId).setTeam(newTeam);
    }

    public HashMap<Integer, String> getDefaultImages() {
        return new HashMap<>(defaultImages);
    }

    public void makePlayerHost(UUID roomId, UUID playerId) {
        Room room = rooms.get(roomId);
        room.switchHostTo(playerId);
    }
}
