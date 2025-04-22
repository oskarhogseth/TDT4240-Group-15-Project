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
import group15.gdx.project.model.LetterSet;
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

    private float timeLeft;
    private boolean timerEnded = false;

    public GameView(Launcher game, GameSession session, Player player) {
        this.game = game;
        this.session = session;
        this.player = player;

        stage = new Stage(new FitViewport(1080, 2400));
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

        timeLeft = getRoundTime();

        setupUI();
    }

    private float getRoundTime() {
        String diff = session.getSelectedDifficulty();
        return "HARD".equalsIgnoreCase(diff) ? 15f : 30f;
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        float baseFont = screenHeight / 40f;

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().padTop(20);
        stage.addActor(rootTable);

        Table timerSection = new Table();
        timerSection.top().padLeft(60);

        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                showLeaveConfirmation();
            }
        });

        timerLabel = new Label("Time: " + (int) timeLeft + "s", skin);
        timerLabel.setFontScale(1.3f);
        timerSection.add(timerLabel).expandX();
        timerSection.add(closeButton).size(40, 40).padRight(20);
        rootTable.add(timerSection).width(screenWidth).height(screenHeight * 0.1f);
        rootTable.row();

        pointsLabel = new Label("YOU HAVE " + player.getScore() + " POINTS", skin);
        pointsLabel.setFontScale(baseFont / 20f);
        rootTable.add(pointsLabel).padTop(10).padBottom(10);
        rootTable.row();

        roundLabel = new Label("Round " + session.getCurrentRound() + " of " + session.getTotalRounds(), skin);
        roundLabel.setFontScale(baseFont / 20f);
        rootTable.add(roundLabel).padBottom(10);
        rootTable.row();

        Table wordBox = new Table();
        wordBox.setBackground(createSolidColorDrawable(1, 1, 1, 0.9f));
        wordLabel = new Label("", skin);
        wordLabel.setColor(Color.BLACK);
        wordLabel.setFontScale(1.8f);
        wordBox.add(wordLabel).expand().fill().pad(10);
        rootTable.add(wordBox).width(300).height(60).colspan(3).center();
        rootTable.row();

        feedbackLabel = new Label("", skin);
        feedbackLabel.setFontScale(1.2f);
        rootTable.add(feedbackLabel).colspan(3).center().padBottom(20);
        rootTable.row();

        pyramidContainer = new Table();
        rootTable.add(pyramidContainer).colspan(3).padBottom(10).center();
        rootTable.row();

        buildPyramid(session.getCurrentLetters().toCharArray());

        Image enterImage = new Image(enterTexture);
        enterButton = new ImageButton(enterImage.getDrawable());
        enterButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (enterButton.isDisabled()) return;
                String word = currentWord.toString().trim();
                if (word.isEmpty()) {
                    feedbackLabel.setText("No word entered.");
                    return;
                }
                if (session.getGuessedWords().contains(word.toLowerCase())) {
                    feedbackLabel.setText("Word already guessed!");
                    resetWord();
                    return;
                }
                boolean valid = session.getGameController().submitWord(player.getName(), word);
                feedbackLabel.setText(valid ? "Word accepted!" : "Invalid word!");
                if (valid) updateScore();
                resetWord();
            }
        });
        rootTable.add(enterButton).width(180).height(40).colspan(3).center();
        rootTable.row();

        Image nextImage = new Image(nextRoundTexture);
        nextRoundButton = new ImageButton(nextImage.getDrawable());
        nextRoundButton.setVisible(false);
        nextRoundButton.setDisabled(true);

        nextRoundButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (session.getCurrentRound() < session.getTotalRounds()) {
                    session.nextRound();
                    LetterSet newSet = session.getGameController().generateLetters();
                    session.getLobby().setCurrentLetterSet(newSet);

                    timeLeft = getRoundTime();
                    timerEnded = false;

                    updateRoundDisplay();
                    buildPyramid(session.getCurrentLetters().toCharArray());

                    resetWord();
                    enterButton.setDisabled(false);

                    for (TextButton btn : letterButtons) {
                        btn.setDisabled(false);
                        btn.setColor(1f, 1f, 1f, 1f);
                    }

                    nextRoundButton.setVisible(false);
                    nextRoundButton.setDisabled(true);
                } else {
                    game.setScreen(new ResultView(game, session, game.getLobbyController()));
                }
            }
        });
        rootTable.add(nextRoundButton).width(180).height(70).colspan(3).center();
    }

    private void showLeaveConfirmation() {
        Image dimOverlay = new Image(createSolidColorDrawable(0, 0, 0, 0.6f));
        dimOverlay.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        stage.addActor(dimOverlay);

        Table dialog = new Table();
        dialog.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("block.png"))));
        dialog.setSize(800, 600);

        Label.LabelStyle labelStyle = new Label.LabelStyle(cinzelFont, Color.BLACK);
        Label msgLabel = new Label("Are you sure you want to leave the game?", labelStyle);
        msgLabel.setWrap(true);
        msgLabel.setAlignment(Align.center);
        dialog.add(msgLabel).width(700).padTop(60).colspan(2).center();
        dialog.row();

        ImageButton leaveBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("leave.png"))));
        leaveBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dialog.remove();
                dimOverlay.remove();

                String pin = session.getLobby().getPin();
                String playerId = session.getLocalPlayer().getId();

                game.getLobbyController().leaveGame(pin, playerId, () -> {
                    Gdx.app.postRunnable(() ->
                            game.setScreen(new LogInView(game, session, game.getLobbyController()))
                    );
                });
            }
        });

        ImageButton cancelBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("cancel.png"))));
        cancelBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dialog.remove();
                dimOverlay.remove();
            }
        });

        dialog.add(leaveBtn).size(240, 120).padTop(60).padRight(40);
        dialog.add(cancelBtn).size(240, 120).padTop(60);
        dialog.setPosition(
                (stage.getViewport().getWorldWidth() - dialog.getWidth()) / 2f,
                (stage.getViewport().getWorldHeight() - dialog.getHeight()) / 2f
        );
        stage.addActor(dialog);
    }

    private void buildPyramid(char[] letters) {
        float tileSize = 90f;
        int index = 0;
        pyramidContainer.clear();
        letterButtons.clear();
        int[] rowLayout = {1, 2, 3};
        for (int rowCount : rowLayout) {
            Table row = new Table();
            for (int i = 0; i < rowCount && index < letters.length; i++) {
                row.add(createTile(letters[index++], tileSize)).size(tileSize).pad(5);
            }
            pyramidContainer.add(row).colspan(3).padBottom(10).center();
            pyramidContainer.row();
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
            @Override public void clicked(InputEvent event, float x, float y) {
                if (tile.isDisabled()) return;
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
                nextRoundButton.setDisabled(false);

                if (session.getCurrentRound() == session.getTotalRounds()) {
                    nextRoundButton.getImage().setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("viewresults.png"))));
                }
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
    }

    private boolean isHost() {
        return session.getLocalPlayer().getUid().equals(session.getLobby().getPlayers().get(0).getUid());
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
