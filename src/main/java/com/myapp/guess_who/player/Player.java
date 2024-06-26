package com.myapp.guess_who.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    private UUID id;
    private String name;
    private boolean host;

    public static Player create(String name) {
        return Player.builder().id(UUID.randomUUID()).name(name).host(false).build();
    }
}
