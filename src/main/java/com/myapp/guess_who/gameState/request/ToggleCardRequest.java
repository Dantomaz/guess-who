package com.myapp.guess_who.gameState.request;

import com.myapp.guess_who.team.Team;

public record ToggleCardRequest(Integer cardNumber, Team team) {

}
