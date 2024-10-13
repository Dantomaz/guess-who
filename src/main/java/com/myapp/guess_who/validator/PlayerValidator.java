package com.myapp.guess_who.validator;

import com.myapp.guess_who.player.Player;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerValidator {

    public void validatePlayerId(UUID playerId) {
        isPlayerIdNotBlank(playerId);
    }

    public void validatePlayer(Player player) {
        validatePlayerId(player.getId());
        isPlayerNameNotBlank(player.getName());
    }

    private void isPlayerIdNotBlank(UUID playerId) {
        if (StringUtils.isBlank(playerId.toString())) {
            throw new IllegalArgumentException("Incorrect player ID (%s)".formatted(playerId));
        }
    }

    private void isPlayerNameNotBlank(String playerName) {
        if (StringUtils.isBlank(playerName)) {
            throw new IllegalArgumentException("Incorrect player name (%s)".formatted(playerName));
        }
    }
}
