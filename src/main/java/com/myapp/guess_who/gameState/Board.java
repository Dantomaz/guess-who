package com.myapp.guess_who.gameState;

import lombok.Data;

import java.util.List;

@Data
public class Board {

    private Integer chosenCardNr;
    private List<Card> cards;
}
