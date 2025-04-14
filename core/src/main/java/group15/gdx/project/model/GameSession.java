package group15.gdx.project.model;

import java.util.ArrayList;
import java.util.List;

import group15.gdx.project.controller.GameController;

public class GameSession {
    private String currentLetters;          // The scrambled letters displayed to the user
    private String activeSortedKey;         // The underlying sorted key (e.g. "aabb")
    private GameLobby lobby;
    private GameController gameController;

    // Track the words guessed in the current round
    private List<String> guessedWords;

    public GameSession() {
        this.lobby = new GameLobby();
        this.currentLetters = "";
        this.activeSortedKey = "";
        this.guessedWords = new ArrayList<>();
        this.gameController = new GameController(this);
    }

    // ---------------------
    // Letters (scrambled)
    // ---------------------
    public String getCurrentLetters() {
        return currentLetters;
    }

    public void setCurrentLetters(String letters) {
        this.currentLetters = letters;
    }

    // ---------------------
    // Sorted Key
    // ---------------------
    public String getActiveSortedKey() {
        return activeSortedKey;
    }

    public void setActiveSortedKey(String key) {
        this.activeSortedKey = key;
    }

    // ---------------------
    // Guessed Words
    // ---------------------
    public List<String> getGuessedWords() {
        return guessedWords;
    }

    // Optionally, a helper if you like:
    public void addGuessedWord(String word) {
        guessedWords.add(word);
    }

    // ---------------------
    // Lobby & Controller
    // ---------------------
    public GameLobby getLobby() {
        return lobby;
    }

    public GameController getGameController() {
        return gameController;
    }
}
