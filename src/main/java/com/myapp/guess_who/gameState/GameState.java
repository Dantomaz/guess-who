package com.myapp.guess_who.gameState;

import com.myapp.guess_who.team.Team;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class GameState {

    public final static int NUMBER_OF_TEAMS = 2;

    private Status status;
    private List<Card> cards;
    private Integer cardNrChosenByBlue;
    private Integer cardNrChosenByRed;
    private Map<UUID, Integer> votesBlue;
    private Map<UUID, Integer> votesRed;
    private Team currentTurn;
    private Team winner;


    public void voteBlue(UUID playerId, int cardNumber) {
        votesBlue.put(playerId, cardNumber);
    }

    public void voteRed(UUID playerId, int cardNumber) {
        votesRed.put(playerId, cardNumber);
    }

    public enum Status {
        NEW,
        VOTING,
        IN_PROGRESS,
        FINISHED
    }
}
