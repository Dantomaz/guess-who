package com.myapp.guess_who.validator;

import com.myapp.guess_who.exception.customException.GameAlreadyInProgressException;
import com.myapp.guess_who.exception.customException.NoSuchRoomException;
import com.myapp.guess_who.gameState.GameState;
import com.myapp.guess_who.room.Room;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RoomValidator {

    public void validateRoomId(UUID roomId, Map<UUID, Room> rooms) {
        isRoomIdNotBlank(roomId);
        isRoomCreated(roomId, rooms);
    }

    public void validateRoom(UUID roomId, Map<UUID, Room> rooms) {
        validateRoomId(roomId, rooms);
        isGameNotStarted(roomId, rooms);
    }

    private void isRoomIdNotBlank(UUID roomId) {
        if (StringUtils.isBlank(roomId.toString())) {
            throw new IllegalArgumentException("Incorrect room ID (%s)".formatted(roomId));
        }
    }

    private void isRoomCreated(UUID roomId, Map<UUID, Room> rooms) {
        if (!rooms.containsKey(roomId)) {
            throw new NoSuchRoomException("Room does not exist (%s)".formatted(roomId));
        }
    }

    private void isGameNotStarted(UUID roomId, Map<UUID, Room> rooms) {
        if (!rooms.get(roomId).getGameState().getStatus().equals(GameState.Status.NEW)) {
            throw new GameAlreadyInProgressException("Game is already in progress (%s)".formatted(roomId));
        }
    }
}
