package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;

import java.util.ArrayList;
import java.util.List;

public class GameView extends ScreenAdapter {
    private final Launcher game;
    private final GameSession session;
    private final String activePlayerName;

    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private BitmapFont cinzelFont;

    private Texture backgroundTexture, blockTexture, enterTexture, nextRoundTexture;

    private Table rootTable;
    private Label wordLabel, feedbackLabel, timerLabel;
    private ImageButton enterButton, nextRoundButton;

    private List<TextButton> letterButtons = new ArrayList<>();
    private StringBuilder currentWord = new StringBuilder();

    private float timeLeft = 30f;
    private boolean timerEnded = false;

    public GameView(Launcher game, GameSession session, String activePlayerName) {
        this.game = game;
        this.session = session;
        this.activePlayerName = activePlayerName;

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
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().padTop(30);
        stage.addActor(rootTable);

        // Timer
        timerLabel = new Label("Time: 30s", skin);
        timerLabel.setFontScale(1.3f);
        rootTable.add(timerLabel).center().colspan(3).padBottom(20);
        rootTable.row();

        // Word field
        Table wordBox = new Table();
        wordBox.setBackground(createSolidColorDrawable(1, 1, 1, 0.9f));
        wordLabel = new Label("", skin);
        wordLabel.setColor(Color.BLACK);
        wordLabel.setFontScale(1.8f);
        wordBox.add(wordLabel).expand().fill().pad(10);
        rootTable.add(wordBox).width(300).height(60).colspan(3).padBottom(10).center();
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
                boolean valid = session.getGameController().submitWord(activePlayerName, word);
                feedbackLabel.setText(valid ? "Word accepted!" : "Invalid word!");
                wordLabel.setText("");
                currentWord.setLength(0);
            }
            //TODO: Sørge for at et ord kun kan brukes en gang <3
        });
        rootTable.add(enterButton).width(180).height(70).padTop(20).colspan(3).center();
        rootTable.row();

        // Next round button
        Image nextImage = new Image(nextRoundTexture);
        nextRoundButton = new ImageButton(nextImage.getDrawable());
        nextRoundButton.setVisible(false);
        nextRoundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ResultView(game, session));
            }
        });
        rootTable.add(nextRoundButton).width(180).height(70).padTop(20).colspan(3).center();
    }

    private void buildPyramid(char[] letters) {
        float tileSize = 90f;
        int index = 0;

        // Row 1 (1 tile)
        if (index < letters.length) {
            Table row1 = new Table();
            row1.add().width(tileSize);
            row1.add(createTile(letters[index++], tileSize)).size(tileSize);
            row1.add().width(tileSize);
            rootTable.add(row1).colspan(3).padTop(50).padBottom(10).center();
            rootTable.row();
        }

        // Row 2 (2 tiles)
        Table row2 = new Table();
        for (int i = 0; i < 2 && index < letters.length; i++) {
            row2.add(createTile(letters[index++], tileSize)).size(tileSize).pad(5);
        }
        rootTable.add(row2).colspan(3).padBottom(10).center();
        rootTable.row();

        // Row 3 (3 tiles)
        Table row3 = new Table();
        for (int i = 0; i < 3 && index < letters.length; i++) {
            row3.add(createTile(letters[index++], tileSize)).size(tileSize).pad(5);
        }
        rootTable.add(row3).colspan(3).padBottom(20).center();
        rootTable.row();
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

                nextRoundButton.setVisible(true);
            }
        }

        stage.act(delta);
        stage.draw();
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
