package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.controller.GameController;

public class GameView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession gameSession;
    private final GameController controller;

    private Stage stage;
    private Skin skin;

    public GameView(Launcher game, GameSession session, group15.gdx.project.model.Player player) {
        this.game = game;
        this.gameSession = session;
        this.controller = gameSession.getGameController(); // Access GameController from GameSession

        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Game Started!", skin);
        table.add(titleLabel).center().padBottom(50);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
