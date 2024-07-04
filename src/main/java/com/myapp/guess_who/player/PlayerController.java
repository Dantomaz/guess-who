package com.myapp.guess_who.player;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class PlayerController {

    @GetMapping("/player/init/{playerName}")
    public ResponseEntity<Player> initPlayer(@PathVariable("playerName") String playerName) {
        return ResponseEntity.ok(Player.create(playerName));
    }
}
