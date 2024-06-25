package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Room {

    private UUID id;
    private List<Player> players;
    private Room.Status status;

    public static Room create(Player host) {
        return Room.builder().id(UUID.randomUUID()).players(List.of(host)).status(Room.Status.NEW).build();
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        FINISHED
    }
}
