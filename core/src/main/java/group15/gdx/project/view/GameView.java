package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;

public class GameView extends ScreenAdapter {
    private final Launcher game;
    private final GameSession session;
    private final String activePlayerName;

    private Stage stage;
    private Skin skin;

    private Label timerLabel;
    private Label pointsLabel;
    private Label selectedWordLabel;
    private Label feedbackLabel;

    private StringBuilder currentWord = new StringBuilder();
    private Array<TextButton> letterButtons = new Array<>();

    // Timer properties
    private float timeLeft = 60; // 60 seconds countdown
    private boolean timerActive = true;

    public GameView(Launcher game, GameSession session, String activePlayerName) {
        this.game = game;
        this.session = session;
        this.activePlayerName = activePlayerName;

        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        float baseFont = screenHeight / 40f; // Relative font size

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        stage.addActor(mainTable);

        // Timer section
        Table timerSection = new Table();
        timerSection.top().padTop(20);

        // Close button (X) - top right
        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ResultView(game, session));
            }
        });

        // Timer label with clock icon
        timerLabel = new Label("37 SECONDS LEFT...", skin);
        timerLabel.setFontScale(baseFont / 18f);

        timerSection.add(new Label("⏱", skin)).padRight(10);
        timerSection.add(timerLabel).expandX();
        timerSection.add(closeButton).size(40, 40).padRight(20);

        mainTable.add(timerSection).width(screenWidth).height(screenHeight * 0.1f);
        mainTable.row();

        // Points display
        pointsLabel = new Label("YOU HAVE " +  "add score here" + " POINTS", skin);
        pointsLabel.setFontScale(baseFont / 20f);
        mainTable.add(pointsLabel).padTop(30).padBottom(40);
        mainTable.row();

        // Word display field (white rectangle in the image)
        selectedWordLabel = new Label("", skin);
        selectedWordLabel.setFontScale(baseFont / 18f);
        Table wordDisplayTable = new Table();
        wordDisplayTable.add(selectedWordLabel).pad(15);

        mainTable.add(wordDisplayTable).width(screenWidth * 0.7f).height(50).padBottom(30);
        mainTable.row();

        // Letter buttons in pyramid arrangement
        createLetterButtonsPyramid(mainTable, screenWidth);

        // Enter word button
        TextButton enterWordButton = new TextButton("ENTER WORD", skin);
        enterWordButton.getLabel().setFontScale(baseFont / 22f);
        enterWordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String typedWord = currentWord.toString().trim();
                if (typedWord.isEmpty()) {
                    feedbackLabel.setText("No word entered.");
                    return;
                }
                boolean result = session.getGameController().submitWord(activePlayerName, typedWord);
                feedbackLabel.setText(result ? "Word accepted!" : "Invalid word!");
                resetWord();
            }
        });

        mainTable.add(enterWordButton)
            .width(screenWidth * 0.5f)
            .height(60)
            .padTop(30);
        mainTable.row();

        // Feedback label
        feedbackLabel = new Label("", skin);
        feedbackLabel.setFontScale(baseFont / 24f);
        mainTable.add(feedbackLabel).padTop(20);
    }

    private void createLetterButtonsPyramid(Table mainTable, float screenWidth) {
        char[] letters = session.getCurrentLetters().toCharArray();
        float buttonSize = screenWidth / 8;

        // First row - 1 button
        Table row1 = new Table();
        addLetterButton(row1, letters[0], buttonSize);
        mainTable.add(row1);
        mainTable.row();

        // Second row - 2 buttons
        Table row2 = new Table();
        addLetterButton(row2, letters[1], buttonSize);
        addLetterButton(row2, letters[2], buttonSize);
        mainTable.add(row2);
        mainTable.row();

        // Third row - 3 buttons
        Table row3 = new Table();
        addLetterButton(row3, letters[3], buttonSize);
        addLetterButton(row3, letters[4], buttonSize);
        addLetterButton(row3, letters[5], buttonSize);
        mainTable.add(row3);
        mainTable.row();
    }

    private void addLetterButton(Table row, char letter, float size) {
        TextButton button = new TextButton(String.valueOf(letter), skin);
        button.getLabel().setFontScale(1.5f);

        // Store which letter this button represents
        final char letterValue = letter;

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentWord.append(letterValue);
                updateWordDisplay();
            }
        });

        letterButtons.add(button);
        row.add(button).size(size, size).pad(5);
    }

    private void updateWordDisplay() {
        selectedWordLabel.setText(currentWord.toString());
    }

    private void resetWord() {
        currentWord.setLength(0);
        updateWordDisplay();
    }

    private void updateTimer(float deltaTime) {
        if (timerActive) {
            timeLeft -= deltaTime;
            if (timeLeft <= 0) {
                timeLeft = 0;
                timerActive = false;
                game.setScreen(new ResultView(game, session));
            }
            timerLabel.setText((int)timeLeft + " SECONDS LEFT...");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateTimer(delta);

        // Update letter buttons if the available letters change
        char[] letters = session.getCurrentLetters().toCharArray();
        for (int i = 0; i < letterButtons.size && i < letters.length; i++) {
            letterButtons.get(i).setText(String.valueOf(letters[i]));
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
