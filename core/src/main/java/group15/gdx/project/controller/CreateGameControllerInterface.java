package group15.gdx.project.controller;

public interface CreateGameControllerInterface {
    void createLobby(String nickname, int rounds, String difficulty, Runnable onSuccess, Runnable onError);
}
