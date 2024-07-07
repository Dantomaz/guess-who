package com.myapp.guess_who.player;

import com.myapp.guess_who.team.Team;
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
    private Team team;

    public static Player createPlayer(String name) {
        return Player.builder().id(UUID.randomUUID()).name(name).host(false).team(Team.SPECTATORS).build();
    }
}
