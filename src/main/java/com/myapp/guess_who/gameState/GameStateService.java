package com.myapp.guess_who.gameState;

import com.myapp.guess_who.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GameStateService {

    public GameState getNewGameState() {
        GameState newGameState = new GameState();
        newGameState.setStatus(GameState.Status.NEW);
        return newGameState;
    }

    public void resetGameState(GameState gameState) {
        gameState.setStatus(GameState.Status.NEW);
        gameState.setCardNrChosenByRed(null);
        gameState.setCardNrChosenByBlue(null);
        gameState.getCards().forEach(Card::reset);
        gameState.setCurrentTurn(null);
        gameState.setWinner(null);
    }

    public void initializeCards(GameState gameState, int size) {
        List<Card> cards = IntStream
            .rangeClosed(1, size)
            .mapToObj(Card::new)
            .toList();
        gameState.setCards(cards);
    }

    private Team chooseRandomTeam() {
        int NUMBER_OF_TEAMS = 2;
        int randomTeam = new Random().nextInt(NUMBER_OF_TEAMS);
        return randomTeam == 0 ? Team.RED : Team.BLUE;
    }
}
