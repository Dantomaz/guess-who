package com.myapp.guess_who.room;

import com.github.fge.jsonpatch.JsonPatch;
import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.utils.JsonPatcher;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RoomManager {

    private final Map<UUID, Room> rooms = new HashMap<>();
    private final JsonPatcher jsonPatcher;

    public Room createRoom(Player host) {
        validatePlayer(host);

        host.setHost(true);
        Room newRoom = Room.create(host);
        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
    }

    public void addPlayer(UUID roomId, Player player) {
        validatePlayer(player);
        validateRoomId(roomId);

        player.setHost(false);
        rooms.get(roomId).addPlayer(player);
    }

    public void removePlayer(UUID roomId, UUID playerId) {
        validatePlayerId(playerId);
        validateRoomId(roomId);

        Room room = rooms.get(roomId);
        room.removePlayer(playerId);
        if (room.getPlayers().isEmpty()) {
            rooms.remove(roomId);
        } else {
            room.chooseNewHost();
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

    public void updatePlayer(UUID roomId, UUID playerId, JsonPatch jsonPatch) {
        Room room = rooms.get(roomId);
        Player updatedPlayer = jsonPatcher.patch(room.getPlayer(playerId), Player.class, jsonPatch);
        updatedPlayer.setId(playerId); // make sure id stays the same
        room.updatePlayer(updatedPlayer);
    }

    private void validatePlayer(Player player) {
        validatePlayerId(player.getId());
        if (StringUtils.isBlank(player.getName())) {
            throw new IllegalArgumentException("Incorrect player name (%s)".formatted(player.getName()));
        }
    }

    private void validatePlayerId(UUID playerId) {
        if (StringUtils.isBlank(playerId.toString())) {
            throw new IllegalArgumentException("Incorrect player ID (%s)".formatted(playerId));
        }
    }

    private void validateRoomId(UUID roomId) {
        if (StringUtils.isBlank(roomId.toString()) || !rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Incorrect room ID (%s)".formatted(roomId));
        }
    }
}
