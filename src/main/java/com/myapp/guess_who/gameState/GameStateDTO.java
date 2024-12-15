package com.myapp.guess_who.gameState;

import com.myapp.guess_who.team.Team;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class GameStateDTO {

    private final GameState.GameStatus gameStatus;
    private final Team currentTurn;
    private final Team winner;
    private final Integer totalNumberOfPlayersVotes;
    private final List<Activity> activityHistory;
    // Below is team specific state, which stays uninitialized for players without a team
    private List<Card> cards;
    private Integer pickedCardNumber;
    private Integer pickedOpponentsCardNumber;
    private Map<UUID, Integer> playersVotes;

    public GameStateDTO(GameState gameState, Team team) {
        gameStatus = gameState.getGameStatus();
        currentTurn = gameState.getCurrentTurn();
        winner = gameState.getWinner();
        totalNumberOfPlayersVotes = gameState.getTotalNumberOfPlayersVotes();
        activityHistory = gameState.getActivityHistory();

        if (Team.NONE.equals(team)) {
            return;
        }

        TeamState teamState = gameState.getTeamState(team);
        if (teamState != null) {
            cards = teamState.getCards();
            pickedCardNumber = teamState.getPickedCardNumber();
            playersVotes = teamState.getPlayersVotes();
        }

        pickedOpponentsCardNumber = gameStatus == GameState.GameStatus.FINISHED ? gameState.getOpponentsCardNumber(team) : null;
    }
}
