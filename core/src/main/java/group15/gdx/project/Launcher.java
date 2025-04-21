package group15.gdx.project;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.controller.LobbyServiceInterface;
import group15.gdx.project.midi.GameSong;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.view.LogInView;

public class Launcher extends Game implements GestureListener {

    private GameSession session;
    private LobbyServiceInterface lobbyService;
    private LobbyController controller;
    private GameSong gameSong;

    private boolean isMuted = false;

    public void setLobbyService(LobbyServiceInterface service) {
        this.lobbyService = service;
    }

    public LobbyController getLobbyController() {
        return controller;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
        if (gameSong != null) {
            if (isMuted) {
                gameSong.stop();
            } else {
                gameSong.play();
            }
        }
    }

    public void toggleMute() {
        setMuted(!isMuted);
    }

    @Override
    public void create() {
        session = new GameSession();
        controller = new LobbyController(lobbyService);
        gameSong = new GameSong();
        gameSong.play(); // Start music at launch

        Gdx.input.setInputProcessor(new GestureDetector(this));

        setScreen(new LogInView(this, session, controller));
    }

    // Gesture methods
    @Override public boolean touchDown(float x, float y, int pointer, int button) { return false; }
    @Override public boolean tap(float x, float y, int count, int button) { return true; }
    @Override public boolean fling(float velocityX, float velocityY, int button) { return true; }
    @Override public boolean pan(float x, float y, float deltaX, float deltaY) { return false; }
    @Override public boolean panStop(float x, float y, int pointer, int button) { return false; }
    @Override public boolean zoom(float initialDistance, float distance) { return false; }
    @Override public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) { return false; }
    @Override public boolean longPress(float x, float y) { return false; }
    @Override public void pinchStop() {}
}
