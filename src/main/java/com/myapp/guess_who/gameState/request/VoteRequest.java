package com.myapp.guess_who.gameState.request;

import java.util.UUID;

public record VoteRequest(UUID playerId, Integer cardNumber) {

}
