package group15.gdx.project.lwjgl3;

import group15.gdx.project.controller.LobbyServiceInterface;

public class DummyLobbyService implements LobbyServiceInterface {

    @Override
    public void createLobby(String pin, String nickname) {
        System.out.println("Dummy: createLobby called with pin=" + pin + ", nickname=" + nickname);
    }

    @Override
    public void joinLobby(String pin, String nickname, Runnable onSuccess, Runnable onFail) {
        System.out.println("Dummy: joinLobby called");
        onSuccess.run(); // Simulate success
    }

    @Override
    public void listenToLobby(String pin, PlayerUpdateCallback callback) {
        System.out.println("Dummy: listenToLobby called");
    }

    @Override
    public void startGame(String pin) {
        System.out.println("Dummy: startGame called");
    }
}
