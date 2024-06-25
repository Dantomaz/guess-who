package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RoomManager {

    private final Map<UUID, Room> rooms;

    public Room createRoom(Player host) {
        Room newRoom = Room.builder().id(UUID.randomUUID()).players(List.of(host)).status(Room.Status.NEW).build();
        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
    }

    public Room addPlayer(Player player, UUID roomId) {
        Room room = rooms.get(roomId);
        room.getPlayers().add(player);
        return room;
    }

    public Room createRoom(UUID roomId) {
        return rooms.remove(roomId);
    }
}
