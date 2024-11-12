package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameState;
import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.player.PlayerService;
import com.myapp.guess_who.utils.FileMappingService;
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
    private final HashMap<Integer, String> defaultImages = new HashMap<>();
    private final PlayerService playerService;
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
        newRoom.setGameState(new GameState());

        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
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
        // Remove the player
        room.removePlayer(playerId);

        if (room.hasNoPlayers()) {
            // Room can be closed if there are no players left
            closeRoom(roomId);
            fileMappingService.cleanUpImages(roomId);
        } else if (room.getPlayers().size() == 1) {
            // Player can't play alone
            room.getGameState().resetGame();
            playerService.chooseNewHostAtRandom(room.getPlayers());
        } else {
            playerService.chooseNewHostAtRandom(room.getPlayers());
        }
    }

    public Room getRoom(UUID roomId) {
        return rooms.get(roomId);
    }

    public HashMap<Integer, String> getDefaultImages() {
        return new HashMap<>(defaultImages);
    }

    public void makePlayerHost(UUID roomId, UUID playerId) {
        Room room = rooms.get(roomId);
        room.switchHostTo(playerId);
    }
}
