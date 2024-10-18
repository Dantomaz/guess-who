package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameState;
import com.myapp.guess_who.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    private UUID id;
    private Map<UUID, Player> players;
    private HashMap<Integer, String> images;
    private GameState gameState;

    public static Room create() {
        return Room.builder().id(UUID.randomUUID()).players(new HashMap<>()).build();
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
}
