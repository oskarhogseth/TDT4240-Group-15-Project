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
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

public class LobbyView extends ScreenAdapter {
    private final Launcher game;
    private final GameSession gameSession;

    private Stage stage;
    private Skin skin;

    public LobbyView(Launcher game, GameSession session) {
        this.game = game;
        this.gameSession = session;

        session.getLobby().addPlayer(new Player("Alice"));
        session.getLobby().addPlayer(new Player("Bob"));

        stage = new Stage(new FitViewport(480, 800)); // Scales correctly across devices
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(screenHeight * 0.05f);
        stage.addActor(table);

        float baseFont = screenHeight / 40f; // Responsive font size

        Label titleLabel = new Label("Welcome to the Lobby!", skin);
        titleLabel.setFontScale(baseFont / 20f); // Make title readable but not too big
        table.add(titleLabel).colspan(2).center().padBottom(screenHeight * 0.03f);
        table.row();

        Label instructionsLabel = new Label("Players in lobby:", skin);
        instructionsLabel.setFontScale(baseFont / 22f);
        table.add(instructionsLabel).colspan(2).center().padBottom(screenHeight * 0.02f);
        table.row();

        for (Player p : gameSession.getLobby().getPlayers()) {
            Label playerLabel = new Label(p.getName(), skin);
            playerLabel.setFontScale(baseFont / 24f);
            table.add(playerLabel).colspan(2).center().pad(5);
            table.row();
        }

        table.add().expandY();
        table.row();

        TextButton startButton = new TextButton("Start Game", skin);
        startButton.getLabel().setFontScale(baseFont / 22f);
        startButton.setColor(0.8f, 0.2f, 0.2f, 1);
        startButton.addListener(event -> {
            if (!startButton.isPressed()) return false;
            gameSession.getGameController().generateLetters();
            game.setScreen(new GameView(game, gameSession, gameSession.getLobby().getPlayers().get(0)));
            return true;
        });

        table.add(startButton)
            .width(screenWidth * 0.5f)
            .height(screenHeight * 0.08f)
            .padBottom(screenHeight * 0.05f)
            .colspan(2)
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
