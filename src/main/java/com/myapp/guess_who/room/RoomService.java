package com.myapp.guess_who.room;

import com.myapp.guess_who.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Log4j2
@Service
public class RoomService {

    private final RoomManager roomManager;

    public Room createRoom(String hostName) {
        if (StringUtils.isBlank(hostName)) {
            throw new IllegalArgumentException("Incorrect player name (%s)".formatted(hostName));
        }
        Player host = Player.createHost(hostName);
        return roomManager.createRoom(host);
    }
}
