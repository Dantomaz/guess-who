package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameStateService;
import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.player.PlayerService;
import com.myapp.guess_who.utils.FileMappingService;
import com.myapp.guess_who.validator.PlayerValidator;
import com.myapp.guess_who.validator.RoomValidator;
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
    private final PlayerService playerService;
    private final GameStateService gameStateService;
    private final RoomValidator roomValidator;
    private final PlayerValidator playerValidator;
    private final FileMappingService fileMappingService;

    public Room createRoom(Player host) {
        playerValidator.validatePlayer(host);

        Room newRoom = Room.create();

        host.setHost(true);
        newRoom.addPlayer(host);
        newRoom.setGameState(gameStateService.getNewGameState());

        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
    }

    public void closeRoom(UUID roomId) {
        rooms.remove(roomId);
    }

    public void addPlayer(UUID roomId, Player player) {
        playerValidator.validatePlayer(player);
        roomValidator.validateRoom(roomId, rooms);

        player.setHost(false);
        rooms.get(roomId).addPlayer(player);
    }

    public void removePlayer(UUID roomId, UUID playerId) {
        playerValidator.validatePlayerId(playerId);
        roomValidator.validateRoomId(roomId, rooms);

        Room room = rooms.get(roomId);
        // Remove the player
        room.removePlayer(playerId);
        if (room.hasNoPlayers()) {
            // If it was the last player, close the room and remove uploaded images
            closeRoom(roomId);
            fileMappingService.cleanUpImages(roomId);
        } else {
            playerService.chooseNewHost(room.getPlayers());
        }
    }

    public Room getRoom(UUID roomId) {
        return rooms.get(roomId);
    }
}
