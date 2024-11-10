package com.myapp.guess_who.player;

import com.myapp.guess_who.team.Team;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class PlayerService {

    public void chooseNewHostAtRandom(Map<UUID, Player> players) {
        UUID randomId = chooseRandomPlayerId(players);
        players.get(randomId).setHost(true);
    }

    private UUID chooseRandomPlayerId(Map<UUID, Player> players) {
        int randomIndex = new Random().nextInt(players.size());
        return players.keySet().stream().toList().get(randomIndex);
    }

    public void changePlayerName(Map<UUID, Player> players, UUID playerId, String newName) {
        players.get(playerId).setName(newName);
    }

    public void changePlayerTeam(Map<UUID, Player> players, UUID playerId, Team newTeam) {
        players.get(playerId).setTeam(newTeam);
    }
}
