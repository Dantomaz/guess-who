package com.myapp.guess_who.room;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class RoomController {

    private final RoomService roomService;

    @MessageMapping("/room/create")
    @SendTo("/topic/room")
    public Room createRoom(String hostName) {
        return roomService.createRoom(hostName);
    }
}
