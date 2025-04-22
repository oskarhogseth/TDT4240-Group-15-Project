package group15.gdx.project.controller;

import java.util.Map;

import group15.gdx.project.model.LetterSet;

public interface LobbyServiceInterface {
    interface PlayerUpdateCallback {
        void onPlayersUpdated(Map<String,String> players);
        void onGameStarted(LetterSet set);
    }
    interface CreateCallback {
        void onSuccess(String pin);
        void onError(String message);
    }
    interface JoinCallback {
        void onSuccess();
        void onError(String message);
    }

    void startGame(String pin, LetterSet letters);

    void createLobby(
        String nickname,
        int rounds,
        String difficulty,
        CreateCallback callback
    );

    void joinLobby(
        String pin,
        String nickname,
        JoinCallback callback
    );

    void listenToLobby(String pin, PlayerUpdateCallback callback);

    void resetLobby(String pin, Runnable onSuccess, Runnable onFail);
    void startGame(String pin);
}
