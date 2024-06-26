package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    private UUID id;
    private List<Player> players;
    private Room.Status status;

    public static Room create(Player host) {
        return Room.builder().id(UUID.randomUUID()).players(new ArrayList<>(Collections.singletonList(host))).status(Room.Status.NEW).build();
    }

    public Room addPlayer(Player player) {
        players.add(player);
        return this;
    }

    public Room removePlayer(Player player) {
        players.remove(player);
        return this;
    }

    public Room chooseNewHost() {
        int index = chooseRandomPlayer();
        players.get(index).setHost(true);
        return this;
    }

    private int chooseRandomPlayer() {
        return new Random().nextInt(players.size());
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        FINISHED
    }
}
