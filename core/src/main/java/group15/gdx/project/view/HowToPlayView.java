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

public class HowToPlayView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession gameSession;
    private final LobbyController controller;

    private final Stage stage;
    private final Skin skin;

    private static final String HOW_TO_PLAY_TEXT =
        "• Guess as many words a possible in each round!\n\n" +
            "• All possible words have between 3 to 7 characters\n\n" +
            "• You can use the same letters multiple times\n\n";


    public HowToPlayView(Launcher game,
                         GameSession session,
                         LobbyController controller) {
        this.game = game;
        this.gameSession = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
    }

    private void setupUI() {
        float sw = stage.getViewport().getWorldWidth();
        float sh = stage.getViewport().getWorldHeight();
        float baseFont = sh / 40f;

        Table table = new Table();
        table.setFillParent(true);
        table.center().pad(20);
        stage.addActor(table);

        // Title
        Label title = new Label("How to Play", skin);
        title.setFontScale(baseFont / 18f);
        table.add(title).padBottom(sh * 0.03f);
        table.row();

        // Instructions
        Label body = new Label(HOW_TO_PLAY_TEXT, skin);
        body.setFontScale(baseFont / 26f);
        body.setWrap(true);
        table.add(body)
            .width(sw * 0.8f)
            .padBottom(sh * 0.05f);
        table.row();

        // Back button
        TextButton back = new TextButton("Back to Lobby", skin);
        back.getLabel().setFontScale(baseFont / 22f);
        back.addListener(evt -> {
            if (!back.isPressed()) return false;
            game.setScreen(new LobbyView(game, gameSession, controller));
            return true;
        });
        table.add(back)
            .width(sw * 0.6f)
            .height(sh * 0.08f);
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
