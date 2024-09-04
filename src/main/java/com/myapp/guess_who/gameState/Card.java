package com.myapp.guess_who.gameState;

import lombok.Data;

@Data
public class Card {

    private final int number;
    private boolean closedByRed = true;
    private boolean closedByBlue = true;

    public void reset() {
        closedByRed = true;
        closedByBlue = true;
    }
}
