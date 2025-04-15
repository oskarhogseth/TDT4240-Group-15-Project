package group15.gdx.project.model;

import group15.gdx.project.controller.GameController;
import java.util.ArrayList;
import java.util.List;

public class GameSession {

    private Player localPlayer;
    private GameLobby lobby;
    private List<Character> currentLetters;
    private GameController gameController;

    public GameSession() {
        this.lobby = new GameLobby();
        this.currentLetters = new ArrayList<>();
        this.gameController = new GameController(this);
    }

    public GameSession(Player player, GameLobby lobby) {
        this.localPlayer = player;
        this.lobby = lobby;
        this.currentLetters = new ArrayList<>();
        this.gameController = new GameController(this);
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void setLocalPlayer(Player player) {
        this.localPlayer = player;
    }

    public GameLobby getLobby() {
        return lobby;
    }

    public void setLobby(GameLobby lobby) {
        this.lobby = lobby;
    }

    public List<Character> getCurrentLetters() {
        return currentLetters;
    }

    public void setCurrentLetters(List<Character> letters) {
        this.currentLetters.clear();
        this.currentLetters.addAll(letters);
    }

    public GameController getGameController() {
        return gameController;
    }
}
