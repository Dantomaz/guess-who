package com.myapp.guess_who.gameState;

import com.myapp.guess_who.room.Room;
import com.myapp.guess_who.team.Team;
import com.myapp.guess_who.utils.MultipartParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GameStateService {

    private final MultipartParsingService multipartParsingService;

    public GameState getNewGameState() {
        GameState newGameState = new GameState();
        newGameState.setStatus(GameState.Status.NEW);
        newGameState.setBoardRed(new Board());
        newGameState.setBoardBlue(new Board());
        return newGameState;
    }

    public void resetGameState(Room room) {
        GameState gameState = room.getGameState();
        gameState.setStatus(GameState.Status.NEW);
        resetBoardState(gameState.getBoardRed());
        resetBoardState(gameState.getBoardBlue());
        gameState.setWinner(null);
    }

    private void resetBoardState(Board board) {
        board.setChosenCardNr(null);
        board.getCards().forEach(card -> card.setOpened(true));
    }

    private Team chooseRandomTeam() {
        int NUMBER_OF_TEAMS = 2;
        int randomTeam = new Random().nextInt(NUMBER_OF_TEAMS);
        return randomTeam == 0 ? Team.RED : Team.BLUE;
    }

    public void uploadImages(GameState gameState, List<MultipartFile> images) {
        gameState.setImages(multipartParsingService.toBytes(images));
        initializeCards(gameState, images.size());
    }

    private void initializeCards(GameState gameState, int size) {
        List<Card> cards = IntStream
            .rangeClosed(1, size)
            .mapToObj(Card::new)
            .toList();

        gameState.getBoardRed().setCards(cards);
        gameState.getBoardBlue().setCards(cards);
    }
}
