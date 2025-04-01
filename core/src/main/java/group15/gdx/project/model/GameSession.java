package group15.gdx.project.model;

import group15.gdx.project.controller.GameController;

public class GameSession {
    private String currentLetters;
    private GameLobby lobby;
    private GameController gameController;

    public GameSession() {
        // Initialize the lobby, letters, and controller
        this.lobby = new GameLobby();
        this.currentLetters = "";
        this.gameController = new GameController(this);
    }

    public String getCurrentLetters() {
        return currentLetters;
    }

    public void setCurrentLetters(String letters) {
        this.currentLetters = letters;
    }

    public GameLobby getLobby() {
        return lobby;
    }

    public GameController getGameController() {
        return gameController;
    }
}
