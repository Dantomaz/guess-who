package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
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

    public Room createRoom(Player host) {
        validatePlayer(host);
        Room newRoom = Room.create(host);
        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
    }

    public Room removeRoom(UUID roomId) {
        rooms.remove(roomId);
        return null;
    }

    public Room addPlayer(Player player, UUID roomId) {
        validatePlayer(player);
        validateRoomId(roomId);
        return rooms.get(roomId).addPlayer(player);
    }

    public Room removePlayer(Player player, UUID roomId) {
        validatePlayer(player);
        validateRoomId(roomId);
        Room room = rooms.get(roomId).removePlayer(player);
        return room.getPlayers().isEmpty() ? removeRoom(room.getId()) : room.chooseNewHost();
    }

    public Room updateRoom(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    public Room updatePlayer(UUID roomId, Player player) {
        return rooms.get(roomId).updatePlayer(player);
    }

    private void validatePlayer(Player player) {
        if (StringUtils.isBlank(player.getName())) {
            throw new IllegalArgumentException("Incorrect player name (%s)".formatted(player.getName()));
        }

        if (StringUtils.isBlank(player.getId().toString())) {
            throw new IllegalArgumentException("Incorrect player ID (%s)".formatted(player.getId()));
        }
    }

    private void validateRoomId(UUID roomId) {
        if (StringUtils.isBlank(roomId.toString()) || !rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Incorrect room ID (%s)".formatted(roomId));
        }
    }
}
