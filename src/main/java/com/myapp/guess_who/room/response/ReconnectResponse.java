package com.myapp.guess_who.room.response;

import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.room.RoomDTO;

public record ReconnectResponse(Player player, RoomDTO room) {

}
