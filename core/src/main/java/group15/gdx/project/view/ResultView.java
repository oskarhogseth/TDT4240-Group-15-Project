package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

public class ResultView extends ScreenAdapter {

    private static final String RESULTS = "Results";
    private static final String BACK_TO_LOBBY = "Back to lobby";

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    private Stage stage;
    private Skin skin;

    public ResultView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        float baseFont = screenHeight / 40f;

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(screenHeight * 0.05f);
        stage.addActor(table);

        Label resultsLabel = new Label(RESULTS, skin);
        resultsLabel.setFontScale(baseFont / 18f);
        table.add(resultsLabel).colspan(2).padBottom(screenHeight * 0.03f).center();
        table.row();

        for (Player p : session.getLobby().getPlayers()) {
            Label scoreLabel = new Label(p.getName() + ": " + p.getScore(), skin);
            scoreLabel.setFontScale(baseFont / 22f);
            table.add(scoreLabel).colspan(2).pad(5).center();
            table.row();
        }

        table.add().expandY();
        table.row();

        TextButton backButton = new TextButton(BACK_TO_LOBBY, skin);
        backButton.getLabel().setFontScale(baseFont / 22f);
        backButton.addListener(event -> {
            if (!backButton.isPressed()) return false;
            game.setScreen(new LobbyView(game, session, controller)); // Reuse same controller
            return true;
        });

        table.add(backButton)
            .colspan(2)
            .padTop(screenHeight * 0.05f)
            .width(screenWidth * 0.5f)
            .height(screenHeight * 0.08f)
            .center();
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
