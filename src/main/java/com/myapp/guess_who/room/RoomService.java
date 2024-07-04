package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
@Service
public class RoomService {

    private final RoomManager roomManager;

    public Room createRoom(Player host) {
        host.setHost(true);
        return roomManager.createRoom(host);
    }

    public Room joinRoom(Player player, UUID roomId) {
        player.setHost(false);
        return roomManager.addPlayer(player, roomId);
    }

    public Room leaveRoom(Player player, UUID roomId) {
        return roomManager.removePlayer(player, roomId);
    }

    public Room updateRoom(Room room) {
        return roomManager.updateRoom(room);
    }

    public Room updatePlayer(UUID roomId, Player player) {
        return roomManager.updatePlayer(roomId, player);
    }
}
