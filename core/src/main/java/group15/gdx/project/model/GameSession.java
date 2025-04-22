package group15.gdx.project.model;

import java.util.ArrayList;
import java.util.List;

import group15.gdx.project.controller.GameController;

public class GameSession {
    private String currentLetters;
    private String activeSortedKey;

    private Player localPlayer;
    private GameLobby lobby;
    private GameController gameController;
    private List<String> guessedWords;

    private int currentRound;
    private int totalRounds = 5; //default

    private String selectedDifficulty = "NORMAL"; // default fallback

    public GameSession() {
        this.lobby           = new GameLobby();
        this.currentLetters  = "";
        this.activeSortedKey = "";
        this.guessedWords    = new ArrayList<>();
        this.currentRound    = 1;
        this.selectedDifficulty = "NORMAL";
        this.gameController  = new GameController(this);
    }

    // Letters
    public String getCurrentLetters() {
        return currentLetters;
    }
    public void setCurrentLetters(String letters) {
        this.currentLetters = letters;
    }

    public String getActiveSortedKey() {
        return activeSortedKey;
    }

    public void setActiveSortedKey(String key) {
        this.activeSortedKey = key;
    }

    // Player
    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void setLocalPlayer(Player player) {
        this.localPlayer = player;
    }

    // Guessed Words
    public List<String> getGuessedWords() {
        return guessedWords;
    }

    public void addGuessedWord(String word) {
        guessedWords.add(word);
    }

    // Lobby & Controller
    public GameLobby getLobby() {
        return lobby;
    }

    public void setLobby(GameLobby lobby) {
        this.lobby = lobby;
    }

    public GameController getGameController() {
        return gameController;
    }

    // Rounds
    public int getCurrentRound() {
        return currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int rounds) {
        this.totalRounds = rounds;
    }

    public void nextRound() {
        if (currentRound < totalRounds) {
            currentRound++;
            guessedWords.clear();
        }
    }

    public void resetGame() {
        this.currentRound = 1;
        this.guessedWords.clear();
        for (Player p : lobby.getPlayers()) {
            p.resetScore();
        }
    }

    // Difficulty (NEW)
    public String getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public void setSelectedDifficulty(String difficulty) {
        this.selectedDifficulty = difficulty;
    }
}
