package group15.gdx.project.controller;

import java.util.Map;

public interface LobbyServiceInterface {
    interface PlayerUpdateCallback {
        void onPlayersUpdated(Map<String, String> players);
        void onGameStarted();
    }

    void createLobby(String pin, String nickname);
    void joinLobby(String pin, String nickname, Runnable onSuccess, Runnable onFail);
    void listenToLobby(String pin, PlayerUpdateCallback callback);
    void startGame(String pin);
}
