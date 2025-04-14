package group15.gdx.project.model;

import java.util.ArrayList;
import java.util.List;

import group15.gdx.project.controller.GameController;

public class GameSession {
    private String currentLetters;
    private String activeSortedKey; // The underlying puzzle key
    private GameLobby lobby;
    private GameController gameController;
    private List<String> guessedWords;

    private int currentRound;    // Current round number
    private int totalRounds;     // Total rounds (e.g. 5)

    public GameSession() {
        this.lobby = new GameLobby();
        this.currentLetters = "";
        this.activeSortedKey = "";
        this.guessedWords = new ArrayList<>();
        this.currentRound = 1;
        this.totalRounds = 5;
        this.gameController = new GameController(this);
    }

    // Letters
    public String getCurrentLetters() {
        return currentLetters;
    }

    public void setCurrentLetters(String letters) {
        this.currentLetters = letters;
    }

    // Active Sorted Key
    public String getActiveSortedKey() {
        return activeSortedKey;
    }

    public void setActiveSortedKey(String key) {
        this.activeSortedKey = key;
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

    public void nextRound() {
        currentRound++;
        // Optionally, clear guessed words for the new round:
        guessedWords.clear();
    }
}
