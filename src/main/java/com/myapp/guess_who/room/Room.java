package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameState;
import com.myapp.guess_who.player.Player;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Data
public class Room {

    private final UUID id;
    private Map<UUID, Player> players = new HashMap<>();
    private Map<Integer, String> images = new HashMap<>();
    private GameState gameState = new GameState();

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

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public void clearImages() {
        images.clear();
    }

    public boolean hasNoHost() {
        return players.values().stream().noneMatch(Player::isHost);
    }

    public void selectNewHostAtRandom() {
        selectNewHost(chooseRandomPlayerId());
    }

    private UUID chooseRandomPlayerId() {
        int randomIndex = new Random().nextInt(players.size());
        return players.keySet().stream().toList().get(randomIndex);
    }

    public void switchHostTo(UUID playerId) {
        clearOldHost();
        selectNewHost(playerId);
    }

    private void clearOldHost() {
        Optional<Player> host = players.values().stream().filter(Player::isHost).findFirst();
        host.ifPresent(player -> player.setHost(false));
    }

    private void selectNewHost(UUID playerId) {
        players.get(playerId).setHost(true);
    }
}
