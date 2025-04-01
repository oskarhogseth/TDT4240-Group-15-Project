package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Arrays;
import java.util.List;

import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

public class LobbyView extends ScreenAdapter {
    private final Launcher game;
    private final GameSession gameSession;

    private Stage stage;
    private Skin skin;

    // Load the dictionary file from assets

    public LobbyView(Launcher game, GameSession session) {
        this.game = game;
        this.gameSession = session;

        // For demonstration, add players to the lobby
        session.getLobby().addPlayer(new Player("Alice"));
        session.getLobby().addPlayer(new Player("Bob"));

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Make sure you have a skin file in your assets folder
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
    }

    private void setupUI() {
        // Make the table fill the screen
        Table table = new Table();
        table.setFillParent(true);

        // If you want everything pinned to the top by default, uncomment:
        // table.top();

        stage.addActor(table);

        // Title Label (at the top)
        Label titleLabel = new Label("Welcome to the Lobby!", skin);
        // Optionally scale up the font a bit:
        titleLabel.setFontScale(1.2f);
        table.add(titleLabel).colspan(2).center().padTop(40).padBottom(20);
        table.row();

        // Instructions Label
        Label instructionsLabel = new Label("Players currently in the lobby:", skin);
        table.add(instructionsLabel).colspan(2).center().padBottom(10);
        table.row();

        // Show players in the lobby (middle)
        for (Player p : gameSession.getLobby().getPlayers()) {
            Label playerLabel = new Label(p.getName(), skin);
            table.add(playerLabel).colspan(2).center().pad(5);
            table.row();
        }

        // Add an expanding "spacer" row to push the button to the bottom
        table.add().expandY();
        table.row();

        // "Start Game" Button (bottom)
        TextButton startButton = new TextButton("Start Game", skin);
        // Give the button a different color (simple approach):
        startButton.setColor(0.8f, 0.2f, 0.2f, 1); // Reddish tone
        // Alternatively, define a custom style in your skin file for a more polished approach.

        startButton.addListener(event -> {
            if (!startButton.isPressed()) return false;

            // Generate letters and go to GameScreen
            gameSession.getGameController().generateLetters();
            String activePlayerName = gameSession.getLobby().getPlayers().get(0).getName(); ////// Må endres på senere
            game.setScreen(new GameView(game, gameSession, activePlayerName));
            List<String> formableWords = gameSession.getGameController().getFormableWords(gameSession.getCurrentLetters());
            System.out.println("Candidate letters: " + gameSession.getCurrentLetters());
            System.out.println("Formable words: " + formableWords);
            return true;
        });
        table.add(startButton).colspan(2).center().padBottom(40);
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
