package com.myapp.guess_who.gameState;

import com.myapp.guess_who.team.Team;
import lombok.Data;

import java.util.List;

@Data
public class GameState {

    private Status status;
    private List<Card> cards;
    private Integer cardNrChosenByRed;
    private Integer cardNrChosenByBlue;
    private Team currentTurn;
    private Team winner;

    public enum Status {
        NEW,
        STARTING,
        IN_PROGRESS,
        FINISHED
    }
}
