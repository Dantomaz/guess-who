package com.myapp.guess_who.gameState;

import com.myapp.guess_who.team.Team;
import lombok.Data;

@Data
public class Card {

    private final int number;
    private boolean closedByBlue = false;
    private boolean closedByRed = false;

    public void reset() {
        closedByBlue = false;
        closedByRed = false;
    }

    public void toggleClose(Team team) {
        if (Team.RED.equals(team)) {
            closedByRed = !closedByRed;
        } else {
            closedByBlue = !closedByBlue;
        }
    }
}
