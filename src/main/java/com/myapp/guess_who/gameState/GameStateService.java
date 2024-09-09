package com.myapp.guess_who.gameState;

import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
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
        gameState.setCardNrChosenByBlue(null);
        gameState.setCardNrChosenByRed(null);
        gameState.setVotesBlue(null);
        gameState.setVotesRed(null);
        gameState.setCurrentTurn(null);
        gameState.setWinner(null);
        if (gameState.getCards() != null) {
            gameState.getCards().forEach(Card::reset);
        }
    }

    public void initializeCards(GameState gameState, int size) {
        List<Card> cards = IntStream
            .rangeClosed(1, size)
            .mapToObj(Card::new)
            .toList();
        gameState.setCards(cards);
    }

    public void prepareGame(GameState gameState) {
        gameState.setVotesBlue(new HashMap<>());
        gameState.setVotesRed(new HashMap<>());
        gameState.setStatus(GameState.Status.VOTING);
    }

    public void voteForCard(GameState gameState, Player player, int cardNumber) {
        if (Team.BLUE.equals(player.getTeam())) {
            gameState.voteBlue(player.getId(), cardNumber);
        } else {
            gameState.voteRed(player.getId(), cardNumber);
        }
    }

    public void startGame(GameState gameState) {
        resolveVotes(gameState);
        gameState.setCurrentTurn(chooseRandomTeam());
        gameState.setStatus(GameState.Status.IN_PROGRESS);
    }

    private Team chooseRandomTeam() {
        int randomTeam = new Random().nextInt(GameState.NUMBER_OF_TEAMS);
        return randomTeam == 0 ? Team.BLUE : Team.RED;
    }

    private void resolveVotes(GameState gameState) {
        gameState.setCardNrChosenByBlue(findCardWithMostVotes(gameState.getVotesBlue()));
        gameState.setCardNrChosenByRed(findCardWithMostVotes(gameState.getVotesRed()));
    }

    private Integer findCardWithMostVotes(Map<UUID, Integer> votes) {
        // Step 1: Group by card numbers
        Map<Integer, Long> voteCountsByCard = votes.entrySet().stream()
            .collect(Collectors.groupingBy(
                Map.Entry::getValue, // group by card number
                Collectors.counting() // count how many votes each card number got
            ));

        // Step 2: Get the biggest votes number
        Long mostVotes = voteCountsByCard.values().stream().max(Long::compare).orElse(0L);

        // Step 3: List cards with the most votes (possible ties)
        List<Integer> topCardNumbers = voteCountsByCard.entrySet().stream()
            .filter(cardEntry -> mostVotes.equals(cardEntry.getValue())) // leave only cards with the most votes
            .map(Map.Entry::getKey) // get card numbers
            .toList(); // make a list of top card numbers

        // Step 4: Pick one card at random in case of a tie
        return topCardNumbers.get(new Random().nextInt(topCardNumbers.size()));
    }

    public void toggleCard(GameState gameState, Integer cardNumber, Team team) {
        gameState.getCards().forEach((card) -> {
            if (card.getNumber() == cardNumber) {
                card.toggleClose(team);
            }
        });
    }

    public void nextTurn(GameState gameState) {
        Team nextTurn = Team.RED.equals(gameState.getCurrentTurn()) ? Team.BLUE : Team.RED;
        gameState.setCurrentTurn(nextTurn);
    }
}
