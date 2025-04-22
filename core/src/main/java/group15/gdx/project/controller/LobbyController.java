package group15.gdx.project.controller;

import group15.gdx.project.model.LetterSet;

public class LobbyController {

    private final LobbyServiceInterface service;

    public LobbyController(LobbyServiceInterface service) {
        this.service = service;
    }

    public void createLobby(
        String nickname,
        int rounds,
        String difficulty,
        LobbyServiceInterface.CreateCallback cb
    ) {
        service.createLobby(nickname, rounds, difficulty, cb);
    }

    public void joinLobby(
        String pin,
        String nickname,
        LobbyServiceInterface.JoinCallback cb
    ) {
        service.joinLobby(pin, nickname, cb);
    }

    public void listenToLobby(String pin, LobbyServiceInterface.PlayerUpdateCallback cb) {
        service.listenToLobby(pin, cb);
    }

    public void startGame(String pin, LetterSet letters) {
        service.startGame(pin, letters);
    }

    public void resetLobby(String pin, Runnable onSuccess, Runnable onFail) {
        service.resetLobby(pin, onSuccess, onFail);
    }
}
