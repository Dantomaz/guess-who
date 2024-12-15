package com.myapp.guess_who.gameState;

import com.myapp.guess_who.player.Player;
import com.myapp.guess_who.team.Team;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class GameState {

    public final static int NUMBER_OF_TEAMS = 2;

    private GameStatus gameStatus = GameState.GameStatus.NEW;
    private Map<Team, TeamState> teamsState = new HashMap<>();
    private int totalNumberOfPlayersVotes;
    private Team currentTurn;
    private Team winner;
    private List<Activity> activityHistory = new ArrayList<>();

    public void prepareGame(int numberOfCards) {
        initializeTeamStates();
        initializeCards(numberOfCards);
        gameStatus = GameState.GameStatus.VOTING;
    }

    private void initializeTeamStates() {
        teamsState.put(Team.BLUE, new TeamState());
        teamsState.put(Team.RED, new TeamState());
    }

    private void initializeCards(int size) {
        teamsState.values().forEach((teamState -> teamState.setCards(IntStream
            .rangeClosed(1, size)
            .mapToObj(Card::new)
            .toList()
        )));
    }

    public void resetGame() {
        initializeTeamStates();
        activityHistory = new ArrayList<>();
        gameStatus = GameState.GameStatus.NEW;
        totalNumberOfPlayersVotes = 0;
        currentTurn = null;
        winner = null;
    }

    public void startGame() {
        resolveVotes();
        currentTurn = chooseRandomTeam();
        gameStatus = GameState.GameStatus.IN_PROGRESS;
    }

    private void resolveVotes() {
        pickCard(Team.BLUE, findCardWithMostVotes(getVotes(Team.BLUE)));
        pickCard(Team.RED, findCardWithMostVotes(getVotes(Team.RED)));
    }

    private void pickCard(Team team, int cardNumber) {
        teamsState.get(team).setPickedCardNumber(cardNumber);
    }

    private Map<UUID, Integer> getVotes(Team team) {
        return teamsState.get(team).getPlayersVotes();
    }

    private Integer findCardWithMostVotes(Map<UUID, Integer> votes) {
        // Step 1: Group by card numbers
        Map<Integer, Long> voteCountsByCard = votes.entrySet().stream()
            .collect(Collectors.groupingBy(
                Map.Entry::getValue, // group by card number
                Collectors.counting() // count how many votes each card number got
            ));

        // Step 2: Get the biggest votes number
        Long mostVotes = voteCountsByCard.values().stream().max(Long::compare).orElse(0L);

        // Step 3: List cards with the most votes (possible ties)
        List<Integer> topCardNumbers = voteCountsByCard.entrySet().stream()
            .filter(cardEntry -> mostVotes.equals(cardEntry.getValue())) // leave only cards with the most votes
            .map(Map.Entry::getKey) // get card numbers
            .toList(); // make a list of top card numbers

        // Step 4: Pick one card at random in case of a tie
        return topCardNumbers.get(new Random().nextInt(topCardNumbers.size()));
    }

    private Team chooseRandomTeam() {
        int randomTeam = new Random().nextInt(NUMBER_OF_TEAMS);
        return randomTeam == 0 ? Team.BLUE : Team.RED;
    }

    public void addPlayerVote(Team team, UUID playerId, int cardNumber) {
        teamsState.get(team).addPlayerVote(playerId, cardNumber);
        totalNumberOfPlayersVotes = teamsState.get(Team.BLUE).getPlayersVotes().size() + teamsState.get(Team.RED).getPlayersVotes().size();
    }

    public void toggleCardByPlayer(Team team, int cardNumber) {
        teamsState.get(team).toggleCardByPlayer(cardNumber);
    }

    public void closeBySystem(Team team, int cardNumber) {
        teamsState.get(team).forceCloseCardBySystem(cardNumber);
    }

    public void endCurrentTurn() {
        currentTurn = getOpponentsTeam(currentTurn);
    }

    public void guessCard(int guessedCardNumber) {
        Team currentTeam = currentTurn;
        Team opponentsTeam = getOpponentsTeam(currentTeam);
        boolean guessedCorrectly = teamsState.get(opponentsTeam).isGuessCorrect(guessedCardNumber);
        if (guessedCorrectly) {
            winner = currentTeam;
            finishGame();
        } else {
            closeBySystem(currentTeam, guessedCardNumber);
            endCurrentTurn();
        }
    }

    private Team getOpponentsTeam(Team currentTeam) {
        return Team.RED.equals(currentTeam) ? Team.BLUE : Team.RED;
    }

    private void finishGame() {
        List<Integer> pickedCardNumbers = List.of(
            teamsState.get(Team.BLUE).getPickedCardNumber(),
            teamsState.get(Team.RED).getPickedCardNumber()
        );
        teamsState.values().forEach((teamState -> teamState.uncoverCards(pickedCardNumbers)));
        gameStatus = GameState.GameStatus.FINISHED;
    }

    public TeamState getTeamState(Team team) {
        return teamsState.get(team);
    }

    public Integer getOpponentsCardNumber(Team currentTeam) {
        return teamsState.get(getOpponentsTeam(currentTeam)).getPickedCardNumber();
    }

    public void saveActivity(Player player) {
        activityHistory.add(Activity.builder().player(player).type(Activity.Type.END_TURN).build());
    }

    public void saveActivity(Player player, int cardNumber) {
        activityHistory.add(Activity.builder().player(player).type(Activity.Type.GUESS_CARD).cardNumber(cardNumber).build());
    }

    public void updatePlayerNameInActivities(UUID playerId, String newName) {
        activityHistory.forEach(activity -> {
            Player currentPlayer = activity.getPlayer();
            if (playerId.equals(currentPlayer.getId())) {
                currentPlayer.setName(newName);
            }
        });
    }

    public enum GameStatus {
        NEW,
        VOTING,
        IN_PROGRESS,
        FINISHED
    }
}
