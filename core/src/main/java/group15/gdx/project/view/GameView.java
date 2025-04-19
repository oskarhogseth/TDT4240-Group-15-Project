package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameView extends ScreenAdapter {
    private final Launcher game;
    private final GameSession session;
    private final Player player;

    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private BitmapFont cinzelFont;

    private Texture backgroundTexture, blockTexture, enterTexture, nextRoundTexture;

    private Table rootTable;
    private Table pyramidContainer;

    private Label wordLabel, feedbackLabel, timerLabel, roundLabel, pointsLabel;
    private ImageButton enterButton, nextRoundButton;

    private List<TextButton> letterButtons = new ArrayList<>();
    private StringBuilder currentWord = new StringBuilder();

    private float timeLeft = 10f;
    private boolean timerEnded = false;

    public GameView(Launcher game, GameSession session, Player player) {
        this.game = game;
        this.session = session;
        this.player = player;

        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        blockTexture = new Texture("block.png");
        enterTexture = new Texture("enterword.png");
        nextRoundTexture = new Texture("nextround.png");

        skin = new Skin(Gdx.files.internal("vhs.json"));
        cinzelFont = loadCinzelFont(32);
        skin.get(Label.LabelStyle.class).font = cinzelFont;
        skin.get(TextButton.TextButtonStyle.class).font = cinzelFont;

        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        float baseFont = screenHeight / 40f; // Relative font size

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().padTop(20);
        stage.addActor(rootTable);

        // Timer section
        Table timerSection = new Table();
        timerSection.top().padLeft(60);

        // Close button (X) - top right
        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ResultView(game, session, game.getLobbyController()));
            }
        });

        // Timer
        timerLabel = new Label("Time: 60s", skin);
        timerLabel.setFontScale(1.3f);

        timerSection.add(timerLabel).expandX();
        timerSection.add(closeButton).size(40, 40).padRight(20);
        rootTable.add(timerSection).width(screenWidth).height(screenHeight * 0.1f);
        rootTable.row();

        // Points display
        //Add real time points on the x
        pointsLabel = new Label("YOU HAVE " + player.getScore() + " POINTS", skin);
        pointsLabel.setFontScale(baseFont / 20f);
        rootTable.add(pointsLabel).padTop(10).padBottom(10);
        rootTable.row();

        // Round display
        roundLabel = new Label("Round " + session.getCurrentRound() + " of " + session.getTotalRounds(), skin);
        roundLabel.setFontScale(baseFont / 20f);
        rootTable.add(roundLabel).padBottom(10);
        rootTable.row();

        // Word field
        Table wordBox = new Table();
        wordBox.setBackground(createSolidColorDrawable(1, 1, 1, 0.9f));
        wordLabel = new Label("", skin);
        wordLabel.setColor(Color.BLACK);
        wordLabel.setFontScale(1.8f);
        wordBox.add(wordLabel).expand().fill().pad(10);
        rootTable.add(wordBox).width(300).height(60).colspan(3).center();
        rootTable.row();

        // Feedback
        feedbackLabel = new Label("", skin);
        feedbackLabel.setFontScale(1.2f);
        rootTable.add(feedbackLabel).colspan(3).center().padBottom(20);
        rootTable.row();

        // Letter pyramid
        buildPyramid(session.getCurrentLetters().toCharArray());

        // Enter word button
        Image enterImage = new Image(enterTexture);
        enterButton = new ImageButton(enterImage.getDrawable());
        enterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String word = currentWord.toString().trim();
                if (word.isEmpty()) {
                    feedbackLabel.setText("No word entered.");
                    return;
                }
                // Check if the word was already guessed.
                if (session.getGuessedWords().contains(word.toLowerCase())) {
                    feedbackLabel.setText("Word already guessed!");
                    resetWord();
                    return;
                }
                boolean valid = session.getGameController().submitWord(player.getName(), word);
                feedbackLabel.setText(valid ? "Word accepted!" : "Invalid word!");
                if (valid) {
                    updateScore();
                }
                resetWord();
            }
        });
        rootTable.add(enterButton).width(180).height(40).colspan(3).center();
        rootTable.row();

        // Next round button
        Image nextImage = new Image(nextRoundTexture);
        nextRoundButton = new ImageButton(nextImage.getDrawable());
        nextRoundButton.setVisible(false);
        nextRoundButton.setDisabled(true);
        nextRoundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (session.getCurrentRound() < session.getTotalRounds()) {
                    // Advance to the next round and update the display.
                    session.nextRound();
                    session.getGameController().generateLetters();
                    // Reset timer and re-enable it
                    timeLeft = 10; // Timer
                    timerEnded = false;
                    updateRoundDisplay();  // Update the round display label.
                    updateLetters();
                    System.out.println("Round " + session.getCurrentRound() + " begins.");
                } else {
                    game.setScreen(new ResultView(game, session, game.getLobbyController()));
                }
            }
        });
        rootTable.add(nextRoundButton).width(180).height(70).colspan(3).center();
    }

    private void updateLetters() {
        // Update letter buttons if the available letters change
        char[] letters = session.getCurrentLetters().toCharArray();
        for (int i = 0; i < letterButtons.size() && i < letters.length; i++) {
            letterButtons.get(i).setText(String.valueOf(letters[i]));
        }
    }

    // build pyramid updated to include all letters
    private void buildPyramid(char[] letters) {
        float tileSize = 90f;
        int index = 0;

        letterButtons.clear();

        // Determine layout based on number of letters
        int total = letters.length;
        int[] rowLayout;

        switch (total) {
            case 3: rowLayout = new int[]{3}; break;
            case 4: rowLayout = new int[]{1, 3}; break;
            case 5: rowLayout = new int[]{2, 3}; break;
            case 6: rowLayout = new int[]{1, 2, 3}; break;
            case 7: rowLayout = new int[]{1, 3, 3}; break;
            default: rowLayout = new int[]{1, 2, 3}; break; // fallback
        }

        for (int rowCount : rowLayout) {
            if (index >= total) break;

            Table row = new Table();

            // Center row with empty padding cells
            int emptyCells = (3 - rowCount); // assuming max 3 tiles per row for center alignment
            for (int i = 0; i < emptyCells; i++) {
                row.add().width(tileSize);
            }

            for (int i = 0; i < rowCount && index < total; i++) {
                row.add(createTile(letters[index++], tileSize)).size(tileSize).pad(5);
            }

            rootTable.add(row).colspan(3).padBottom(10).center();
            rootTable.row();
        }
    }


    private TextButton createTile(char letter, float size) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(blockTexture));
        style.font = cinzelFont;

        TextButton tile = new TextButton(String.valueOf(letter), style);
        tile.getLabel().setFontScale(2f);
        tile.setSize(size, size);

        tile.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentWord.append(letter);
                wordLabel.setText(currentWord.toString());
            }
        });

        letterButtons.add(tile);
        return tile;
    }

    private Drawable createSolidColorDrawable(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private BitmapFont loadCinzelFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("cinzel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = Color.BLACK;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0,
            stage.getViewport().getWorldWidth(),
            stage.getViewport().getWorldHeight());
        batch.end();

        if (!timerEnded) {
            timeLeft -= delta;
            int seconds = Math.max(0, (int) timeLeft);
            timerLabel.setText("Time: " + seconds + "s");

            if (timeLeft <= 0) {
                timerEnded = true;
                timerLabel.setText("Time's up!");

                for (TextButton button : letterButtons) {
                    button.setDisabled(true);
                    button.setColor(0.5f, 0.5f, 0.5f, 1);
                }

                enterButton.setDisabled(true);
                // Next round button appears
                nextRoundButton.setVisible(true);
                nextRoundButton.setDisabled(false);
            }
        }

        stage.act(delta);
        stage.draw();
    }
    private void updateWordDisplay() {
        wordLabel.setText(currentWord.toString());
    }

    private void resetWord() {
        currentWord.setLength(0);
        updateWordDisplay();
        System.out.println("current word: "+currentWord);
        System.out.println("wordlabel: "+wordLabel);
    }

    public void updateScore() {
        pointsLabel.setText("YOU HAVE " + player.getScore() + " POINTS");
    }

    private void updateRoundDisplay() {
        roundLabel.setText("Round " + session.getCurrentRound() + " of " + session.getTotalRounds());
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        cinzelFont.dispose();
        backgroundTexture.dispose();
        blockTexture.dispose();
        enterTexture.dispose();
        nextRoundTexture.dispose();
    }
}
