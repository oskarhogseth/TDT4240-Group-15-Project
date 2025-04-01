package group15.gdx.project;

import group15.gdx.project.model.GameSession;
import group15.gdx.project.view.LobbyView;

import com.badlogic.gdx.Game;

public class Launcher extends Game {

    private GameSession session;

    @Override
    public void create() {
        // Create a shared session (model) for all screens
        session = new GameSession();

        // Go directly to the LobbyScreen
        setScreen(new LobbyView(this, session));
    }
}
