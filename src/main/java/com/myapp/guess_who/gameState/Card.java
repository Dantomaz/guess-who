package com.myapp.guess_who.gameState;

import lombok.Data;

@Data
public class Card {

    private final int number;
    private boolean closed = false;
    private boolean closedLocked = false; // if a player guessed incorrect card, then the card is closed by system (can't change it's closed state)

    public void open() {
        closed = false;
        closedLocked = false;
    }

    public void toggleCloseByPlayer() {
        if (closedLocked) {
            return;
        }
        closed = !closed;
    }

    public void forceCloseBySystem() {
        closed = true;
        closedLocked = true;
    }
}
