package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    private UUID id;
    private Map<UUID, Player> players;
    private Room.Status status;

    public static Room create(Player host) {
        return Room.builder().id(UUID.randomUUID()).players(new HashMap<>(Map.of(host.getId(), host))).status(Room.Status.NEW).build();
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

    public void chooseNewHost() {
        UUID randomId = chooseRandomPlayerId();
        players.get(randomId).setHost(true);
    }

    private UUID chooseRandomPlayerId() {
        int randomIndex = new Random().nextInt(players.size());
        return players.keySet().stream().toList().get(randomIndex);
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        FINISHED
    }
}
