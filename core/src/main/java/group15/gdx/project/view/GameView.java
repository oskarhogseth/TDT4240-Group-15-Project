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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;

public class GameView extends ScreenAdapter {
    private final Launcher game;
    private final GameSession session;
    private final String activePlayerName;

    private Stage stage;
    private Skin skin;

    private Label lettersLabel;
    private TextField wordInput;
    private Label feedbackLabel;
    private Label playerNameLabel;

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

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(screenHeight * 0.05f);
        stage.addActor(table);

        // Title / letters
        lettersLabel = new Label("Letters: " + session.getCurrentLetters(), skin);
        lettersLabel.setFontScale(baseFont / 18f);
        table.add(lettersLabel).colspan(2).center().padBottom(screenHeight * 0.03f);
        table.row();

        // Word input + Submit
        wordInput = new TextField("", skin);
        wordInput.setMessageText("Enter a word");
        table.add(wordInput)
            .width(screenWidth * 0.6f)
            .height(screenHeight * 0.08f)
            .padRight(screenWidth * 0.02f);

        TextButton submitButton = new TextButton("Submit", skin);
        submitButton.getLabel().setFontScale(baseFont / 22f);
        table.add(submitButton)
            .width(screenWidth * 0.3f)
            .height(screenHeight * 0.08f);
        table.row();

        // Feedback
        feedbackLabel = new Label("", skin);
        feedbackLabel.setFontScale(baseFont / 24f);
        table.add(feedbackLabel).colspan(2).center().padTop(screenHeight * 0.03f);
        table.row();

        // End Game button
        TextButton endButton = new TextButton("End Game", skin);
        endButton.getLabel().setFontScale(baseFont / 22f);
        endButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ResultView(game, session));
            }
        });
        table.add(endButton)
            .colspan(2)
            .center()
            .padTop(screenHeight * 0.05f)
            .width(screenWidth * 0.5f)
            .height(screenHeight * 0.08f);

        // Submit listener
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String typedWord = wordInput.getText().trim();
                if (typedWord.isEmpty()) {
                    feedbackLabel.setText("No word entered.");
                    return;
                }
                boolean result = session.getGameController().submitWord(activePlayerName, typedWord);
                feedbackLabel.setText(result ? "Word accepted!" : "Invalid word!");
                wordInput.setText("");
            }
        });

        // Player name label (floating top-left)
        playerNameLabel = new Label("Player: " + activePlayerName, skin);
        playerNameLabel.setFontScale(baseFont / 22f);
        playerNameLabel.setPosition(10, screenHeight - (screenHeight * 0.05f));
        stage.addActor(playerNameLabel);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        lettersLabel.setText("Letters: " + session.getCurrentLetters());
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
