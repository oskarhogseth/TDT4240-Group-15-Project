package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.controller.GameController;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final GameController controller;
    private final Player localPlayer;

    private Stage stage;
    private Skin skin;
    private StringBuilder currentWord;
    private Label wordDisplay;
    private Table letterTable;

    public GameView(Launcher game, GameSession session, Player player) {
        this.game = game;
        this.session = session;
        this.localPlayer = player;
        this.controller = session.getGameController();
        this.currentWord = new StringBuilder();

        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        controller.generateLetters();
        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        float baseFont = screenHeight / 40f;

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().padTop(screenHeight * 0.05f);
        stage.addActor(rootTable);

        // Title
        Label title = new Label("Form a Word", skin);
        title.setFontScale(baseFont / 20f);
        rootTable.add(title).colspan(2).center().padBottom(screenHeight * 0.03f);
        rootTable.row();

        // Word being built
        wordDisplay = new Label("", skin);
        wordDisplay.setFontScale(baseFont / 18f);
        rootTable.add(wordDisplay).colspan(2).center().padBottom(screenHeight * 0.03f);
        rootTable.row();

        // Letter tiles
        letterTable = new Table();
        rootTable.add(letterTable).colspan(2).center().padBottom(screenHeight * 0.03f);
        rootTable.row();

        createLetterButtons();

        // Enter & Clear buttons
        TextButton enterButton = new TextButton("Enter Word", skin);
        TextButton clearButton = new TextButton("Clear", skin);

        enterButton.getLabel().setFontScale(baseFont / 24f);
        clearButton.getLabel().setFontScale(baseFont / 24f);

        enterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String submitted = currentWord.toString();
                boolean valid = controller.submitWord(localPlayer, submitted);
                if (valid) {
                    Gdx.app.log("Word Submission", "Accepted: " + submitted);
                    currentWord.setLength(0);
                    wordDisplay.setText("");
                    createLetterButtons();
                } else {
                    Gdx.app.log("Word Submission", "Rejected: " + submitted);
                }
            }
        });

        clearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentWord.setLength(0);
                wordDisplay.setText("");
                createLetterButtons();
            }
        });

        Table buttonTable = new Table();
        buttonTable.add(enterButton).padRight(30).width(screenWidth * 0.35f).height(screenHeight * 0.07f);
        buttonTable.add(clearButton).width(screenWidth * 0.35f).height(screenHeight * 0.07f);
        rootTable.add(buttonTable).colspan(2).center();
    }

    private void createLetterButtons() {
        letterTable.clear();
        List<Character> letters = session.getCurrentLetters();
        int columns = 4;
        int count = 0;

        for (Character letter : letters) {
            final char c = letter;
            TextButton button = new TextButton(String.valueOf(c), skin);
            button.getLabel().setFontScale(1.5f);
            button.pad(10);

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentWord.append(c);
                    wordDisplay.setText(currentWord.toString());
                }
            });

            letterTable.add(button).pad(10).width(80).height(80);
            count++;
            if (count % columns == 0) letterTable.row();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
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
