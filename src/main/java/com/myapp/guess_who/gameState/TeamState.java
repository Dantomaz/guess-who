package com.myapp.guess_who.gameState;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class TeamState {

    private List<Card> cards;
    private int pickedCardNumber;
    private Map<UUID, Integer> playersVotes = new HashMap<>();

    public void addPlayerVote(UUID playerId, int cardNumber) {
        playersVotes.put(playerId, cardNumber);
    }

    public void toggleCardByPlayer(int cardNumber) {
        cards.forEach((card) -> {
            if (card.getNumber() == cardNumber) {
                card.toggleCloseByPlayer();
            }
        });
    }

    public void forceCloseCardBySystem(int cardNumber) {
        cards.forEach((card) -> {
            if (card.getNumber() == cardNumber) {
                card.forceCloseBySystem();
            }
        });
    }

    public void uncoverCards(List<Integer> cardNumbersToUncover) {
        cards.forEach((card) -> {
            if (cardNumbersToUncover.contains(card.getNumber())) {
                card.open();
            }
        });
    }

    public boolean isGuessCorrect(int guessedCardNumber) {
        return pickedCardNumber == guessedCardNumber;
    }
}
