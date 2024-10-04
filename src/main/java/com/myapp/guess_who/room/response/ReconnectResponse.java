package com.myapp.guess_who.room.response;

import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.room.Room;

public record ReconnectResponse(Player player, Room room) {

}
