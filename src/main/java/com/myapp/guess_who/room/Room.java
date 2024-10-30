package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameState;
import com.myapp.guess_who.player.Player;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Room {

    private final UUID id;
    private Map<UUID, Player> players = new HashMap<>();
    private HashMap<Integer, String> images = new HashMap<>();
    private GameState gameState;

    private Room(UUID id) {
        this.id = id;
    }

    public static Room create() {
        return new Room(UUID.randomUUID());
    }

    public Player getPlayer(UUID playerId) {
        return players.get(playerId);
    }

    public void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public void removePlayer(UUID playerId) {
        players.remove(playerId);
    }

    public void updatePlayer(Player player) {
        players.put(player.getId(), player);
    }

    public boolean hasNoPlayers() {
        return players.isEmpty();
    }

    public void clearImages() {
        images.clear();
    }
}
