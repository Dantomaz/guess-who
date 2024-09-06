package com.myapp.guess_who.gameState;

import lombok.Data;

@Data
public class Card {

    private final int number;
    private boolean closedByBlue = true;
    private boolean closedByRed = true;

    public void reset() {
        closedByBlue = true;
        closedByRed = true;
    }
}
