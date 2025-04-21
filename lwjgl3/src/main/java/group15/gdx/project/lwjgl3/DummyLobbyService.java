package group15.gdx.project.lwjgl3;

import group15.gdx.project.controller.LobbyServiceInterface;
import group15.gdx.project.model.LetterSet;

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
    public void startGame(String pin, LetterSet letters) {

    }

    @Override
    public void createLobby(String nickname, int rounds, String difficulty, CreateCallback callback) {

    }

    @Override
    public void joinLobby(String pin, String nickname, JoinCallback callback) {

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
