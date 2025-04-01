package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import group15.gdx.project.Launcher;
import group15.gdx.project.controller.GameController;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen extends ScreenAdapter {
    private final Launcher game;
    private final GameSession session;

    private Stage stage;
    private Skin skin;

    private Label lettersLabel;
    private TextField wordInput;
    private Label feedbackLabel;

    public GameScreen(Launcher game, GameSession session) {
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

        // Display current letters
        lettersLabel = new Label("Letters: " + session.getCurrentLetters(), skin);
        table.add(lettersLabel).colspan(2).padBottom(20);
        table.row();

        // TextField for entering words
        wordInput = new TextField("", skin);
        wordInput.setMessageText("Enter a word");
        table.add(wordInput).width(200).padRight(10);

        // Button to submit the word
        TextButton submitButton = new TextButton("Submit", skin);
        table.add(submitButton).width(100);
        table.row();

        // Label to show feedback (accepted/invalid)
        feedbackLabel = new Label("", skin);
        table.add(feedbackLabel).colspan(2).padTop(20);
        table.row();

        // Button to end game and go to results
        TextButton endButton = new TextButton("End Game", skin);
        endButton.addListener(event -> {
            if (!endButton.isPressed()) return false;
            game.setScreen(new ResultView(game, session));
            return true;
        });
        table.add(endButton).colspan(2).padTop(30);

        // Submit logic
        submitButton.addListener(event -> {
            if (!submitButton.isPressed()) return false;
            String typedWord = wordInput.getText().trim();
            if (typedWord.isEmpty()) {
                feedbackLabel.setText("No word entered.");
                return true;
            }

            // For demo, we submit as "Alice". In a real game, you'd track which player is active.
            boolean result = session.getGameController().submitWord("Alice", typedWord);
            feedbackLabel.setText(result ? "Word accepted!" : "Invalid word!");
            wordInput.setText(""); // Clear the field
            return true;
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update letters label if needed
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
