package com.myapp.guess_who.room;

import com.myapp.guess_who.gameState.GameState;
import com.myapp.guess_who.player.Player;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void changePlayerConnectedStatus(UUID playerId, boolean connectedStatus) {
        players.get(playerId).setConnected(connectedStatus);
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean hasPlayer(UUID playerId) {
        return players.containsKey(playerId);
    }

    public void clearImages() {
        images.clear();
    }

    /**
     * @return true if one and only one host exists and is connected
     */
    public boolean hasViableHost() {
        List<Player> hosts = players.values().stream().filter(Player::isHost).toList();
        if (hosts.size() != 1) {
            return false;
        }
        return hosts.getFirst().isConnected();
    }

    public void switchHostTo(UUID playerId) {
        clearHost();
        selectNewHost(playerId);
    }

    private void clearHost() {
        players.forEach((playerId, player) -> player.setHost(false));
    }

    private void selectNewHost(UUID playerId) {
        players.get(playerId).setHost(true);
    }

    public void selectNewHostAtRandom() {
        clearHost();
        selectNewHost(chooseViableHostPlayerId());
    }

    private UUID chooseViableHostPlayerId() {
        List<Player> connectedPlayers = players.values().stream().filter(Player::isConnected).toList();
        List<Player> playerPoolToChooseFrom = connectedPlayers.isEmpty() ? players.values().stream().toList() : connectedPlayers;
        int randomIndex = new Random().nextInt(playerPoolToChooseFrom.size());
        return playerPoolToChooseFrom.get(randomIndex).getId();
    }
}
