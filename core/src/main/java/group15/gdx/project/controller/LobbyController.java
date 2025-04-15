package group15.gdx.project.controller;

public class LobbyController {

    private final LobbyServiceInterface service;

    public LobbyController(LobbyServiceInterface service) {
        this.service = service;
    }

    public void createLobby(String pin, String nickname) {
        service.createLobby(pin, nickname);
    }

    public void joinLobby(String pin, String nickname, Runnable onSuccess, Runnable onFail) {
        service.joinLobby(pin, nickname, onSuccess, onFail);
    }

    public void listenToLobby(String pin, LobbyServiceInterface.PlayerUpdateCallback callback) {
        service.listenToLobby(pin, callback);
    }

    public void startGame(String pin) {
        service.startGame(pin);
    }
}
