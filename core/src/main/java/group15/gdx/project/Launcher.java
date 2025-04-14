package group15.gdx.project;

import group15.gdx.project.model.GameSession;
import group15.gdx.project.view.LobbyView;
import group15.gdx.project.view.LogInView;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class Launcher extends Game implements GestureListener {

    private GameSession session;

    @Override
    public void create() {
        session = new GameSession();
        Gdx.input.setInputProcessor(new GestureDetector(this)); // Enable touch input
        setScreen(new LogInView(this, session));
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        System.out.println("Tapped at: " + x + ", " + y);
        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        System.out.println("Fling detected!");
        return true;
    }

    // Implement other required methods
    @Override public boolean pan(float x, float y, float deltaX, float deltaY) { return false; }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override public boolean zoom(float initialDistance, float distance) { return false; }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    public boolean pinch(float initialPointer1X, float initialPointer1Y, float initialPointer2X, float initialPointer2Y, float pointer1X, float pointer1Y, float pointer2X, float pointer2Y) { return false; }
    @Override public boolean longPress(float x, float y) { return false; }

    @Override
    public void pinchStop() {
        // No action needed, but must be defined
    }
}
