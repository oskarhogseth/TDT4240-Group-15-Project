package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

public class ResultView extends ScreenAdapter {
    private final Launcher game;
    private final GameSession session;

    private Stage stage;
    private Skin skin;

    public ResultView(Launcher game, GameSession session) {
        this.game = game;
        this.session = session;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Title
        Label resultsLabel = new Label("Results", skin);
        table.add(resultsLabel).colspan(2).padBottom(20);
        table.row();

        // Show scores for each player
        for (Player p : session.getLobby().getPlayers()) {
            table.add(new Label(p.getName() + ": " + p.getScore(), skin)).colspan(2).pad(5);
            table.row();
        }

        // Button to go back to Lobby (or restart)
        TextButton backButton = new TextButton("Back to Lobby", skin);
        backButton.addListener(event -> {
            if (!backButton.isPressed()) return false;

            // For a quick replay, reset scores or create a new session
            // session.getLobby().getPlayers().forEach(player -> player.addScore(-player.getScore()));

            game.setScreen(new LobbyView(game, session));
            return true;
        });
        table.add(backButton).colspan(2).padTop(30);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
