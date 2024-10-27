package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameStateDTO;
import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.team.Team;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class RoomDTO {

    private final UUID id;
    private final Map<UUID, Player> players;
    private final HashMap<Integer, String> images;
    private final GameStateDTO gameState;

    public RoomDTO(Room room, Team team) {
        id = room.getId();
        players = room.getPlayers();
        images = room.getImages();
        gameState = new GameStateDTO(room.getGameState(), team);
    }
}
