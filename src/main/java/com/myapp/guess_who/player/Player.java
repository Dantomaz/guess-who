package com.myapp.guess_who.player;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Player {

    private UUID id;
    private String name;
    private boolean host;

    public static Player create(String name) {
        return Player.builder().id(UUID.randomUUID()).name(name).host(false).build();
    }

    public static Player createHost(String name) {
        return Player.builder().id(UUID.randomUUID()).name(name).host(true).build();
    }
}
