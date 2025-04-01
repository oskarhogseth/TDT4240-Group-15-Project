package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameView extends ScreenAdapter {
    private final Launcher game;
    private final GameSession session;
    private final String activePlayerName; // The player's name (e.g., "Alice" or "Bob")

    private Stage stage;
    private Skin skin;

    private Label lettersLabel;
    private TextField wordInput;
    private Label feedbackLabel;
    private Label playerNameLabel; // New label to display the active player

    public GameView(Launcher game, GameSession session, String activePlayerName) {
        this.game = game;
        this.session = session;
        this.activePlayerName = activePlayerName;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
    }

    private void setupUI() {
        // Create the table for central UI elements
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

        // Make sure the TextField can receive keyboard input
        wordInput.setFocusTraversal(false);
        stage.setKeyboardFocus(wordInput);

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

        // Replace your old submit listener with a ClickListener
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Get the typed word from the TextField and trim extra spaces.
                String typedWord = wordInput.getText().trim();

                // Check if the field is empty.
                if (typedWord.isEmpty()) {
                    feedbackLabel.setText("No word entered.");
                    return;
                }

                // Submit the word for the active player and capture the result.
                boolean result = session.getGameController().submitWord(activePlayerName, typedWord);

                // Create a debug message indicating accepted or rejected.
                String debugMessage = "Typed word is: [" + typedWord + "] " + (result ? "accepted" : "rejected");
                System.out.println(debugMessage);

                // Update the UI with feedback.
                feedbackLabel.setText(result ? "Word accepted!" : "Invalid word!");
                wordInput.setText(""); // Clear the field after submission.
            }
        });

        // Fixed-position label to indicate the current player (top-left corner)
        playerNameLabel = new Label("Player: " + activePlayerName, skin);
        playerNameLabel.setPosition(10, Gdx.graphics.getHeight() - playerNameLabel.getHeight() - 10);
        stage.addActor(playerNameLabel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the letters label if needed
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
