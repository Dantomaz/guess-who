package com.myapp.guess_who.player;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class PlayerService {

    public void chooseNewHost(Map<UUID, Player> players) {
        UUID randomId = chooseRandomPlayerId(players);
        players.get(randomId).setHost(true);
    }

    private UUID chooseRandomPlayerId(Map<UUID, Player> players) {
        int randomIndex = new Random().nextInt(players.size());
        return players.keySet().stream().toList().get(randomIndex);
    }
}
