package com.myapp.guess_who.gameState;

import com.myapp.guess_who.team.Team;
import lombok.Data;

import java.util.HashMap;

@Data
public class GameState {

    private Status status;
    private Board boardRed;
    private Board boardBlue;
    private HashMap<Integer, byte[]> images;
    private Team currentTurn;
    private Team winner;

    public enum Status {
        NEW,
        IN_PROGRESS,
        FINISHED
    }
}
