package com.myapp.guess_who.room;

import com.github.fge.jsonpatch.JsonPatch;
import com.myapp.guess_who.gameState.GameStateService;
import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.player.PlayerService;
import com.myapp.guess_who.utils.JsonPatcher;
import com.myapp.guess_who.validator.PlayerValidator;
import com.myapp.guess_who.validator.RoomValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RoomManager {

    private final Map<UUID, Room> rooms = new HashMap<>();
    private final JsonPatcher jsonPatcher;
    private final PlayerService playerService;
    private final GameStateService gameStateService;
    private final RoomValidator roomValidator;
    private final PlayerValidator playerValidator;

    public Room createRoom(Player host) {
        playerValidator.validatePlayer(host);

        Room newRoom = Room.create();

        host.setHost(true);
        newRoom.addPlayer(host);
        newRoom.setGameState(gameStateService.getNewGameState());

        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
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
        room.removePlayer(playerId);
        if (room.getPlayers().isEmpty()) {
            rooms.remove(roomId);
        } else {
            playerService.chooseNewHost(room.getPlayers());
        }
    }

    public Room getRoom(UUID roomId) {
        return rooms.get(roomId);
    }

    public void updateRoom(UUID roomId, JsonPatch jsonPatch) {
        Room room = rooms.get(roomId);
        Room updated = jsonPatcher.patch(room, Room.class, jsonPatch);
        updated.setId(roomId); // make sure id stays the same
        rooms.put(roomId, updated);
    }
}
