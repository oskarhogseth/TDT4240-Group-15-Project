package group15.gdx.project.controller;

import group15.gdx.project.model.*;
import java.util.Map;

public class LobbyController implements CreateGameControllerInterface {

    private final LobbyServiceInterface service;
    private final GameSession session;

    public LobbyController(LobbyServiceInterface service, GameSession session) {
        this.service = service;
        this.session = session;
    }

    @Override
    public void createLobby(String nickname, int rounds, String difficulty, Runnable onSuccess, Runnable onError) {
        service.createLobby(nickname, rounds, difficulty, new LobbyServiceInterface.CreateCallback() {
            @Override
            public void onSuccess(String pin) {
                Player host = new Player(nickname, nickname);
                session.setLocalPlayer(host);
                session.setTotalRounds(rounds);
                session.getLobby().setPin(pin);
                session.getLobby().updatePlayersFromMap(Map.of(host.getId(), nickname));
                onSuccess.run();
            }

            @Override
            public void onError(String msg) {
                onError.run();
            }
        });
    }

    public void joinLobby(String pin, String nickname, LobbyServiceInterface.JoinCallback cb) {
        service.joinLobby(pin, nickname, cb);
    }

    public void leaveGame(String pin, String playerId, Runnable onComplete) {
        service.leaveGame(pin, playerId, onComplete);
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
