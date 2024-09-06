package com.myapp.guess_who.gameState;

import java.util.UUID;

public record VoteRequest(UUID playerId, Integer cardNumber) {

}
