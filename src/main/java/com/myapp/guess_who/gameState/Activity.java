package com.myapp.guess_who.gameState;

import com.myapp.guess_who.player.Player;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Activity {

    private Player player;
    private Type type;
    private Integer cardNumber;

    public enum Type {
        END_TURN,
        GUESS_CARD
    }
}
