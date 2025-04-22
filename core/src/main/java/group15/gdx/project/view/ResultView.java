package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;
import group15.gdx.project.model.Score;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ResultView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;

    private Texture backgroundTexture;
    private Texture playAgainTexture;
    private Texture backTexture;
    private Texture volumeTexture;
    private Texture muteTexture;
    private Texture logoTexture;

    private ImageButton volumeButton;
    private BitmapFont font;
    private BitmapFont boldFont;

    public ResultView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(1080, 2400));
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture("background.png");
        playAgainTexture = new Texture("playagain.png");
        backTexture = new Texture("back.png");
        volumeTexture = new Texture("volume.png");
        muteTexture = new Texture("mute.png");
        logoTexture = new Texture("wordduel.png");

        skin = new Skin(Gdx.files.internal("vhs.json"));
        font = loadFont("cinzel.ttf", 40);
        boldFont = loadFont("cinzel_bold.ttf", 54);
        skin.get(Label.LabelStyle.class).font = font;

        setupUI();
        setupVolumeToggle();
    }

    private void setupUI() {
        float sw = stage.getViewport().getWorldWidth();
        float sh = stage.getViewport().getWorldHeight();

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(620);
        stage.addActor(root);

        // Title
        Label title = new Label("ROUND RESULTS", new Label.LabelStyle(boldFont, Color.BLACK));
        title.setFontScale(1.6f);
        root.add(title).padBottom(50).colspan(2).center();
        root.row();

        // Ranked Scores
        List<Player> sortedPlayers = session.getLobby().getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());

        int rank = 1;
        for (Player player : sortedPlayers) {
            game.getAPI().submitScore(new Score(player.getName(), player.getScore()));

            Label scoreLabel = new Label(rank + ". " + player.getName() + ": " + player.getScore(), skin);
            scoreLabel.setColor(Color.BLACK);
            scoreLabel.setFontScale(1.2f);
            root.add(scoreLabel).padBottom(20).colspan(2);
            root.row();
            rank++;
        }

        // Buttons row
        Table buttonRow = new Table();
        buttonRow.padTop(80);

        ImageButton playAgain = new ImageButton(new TextureRegionDrawable(new TextureRegion(playAgainTexture)));
        playAgain.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                session.getGameController().generateLetters();
                game.setScreen(new LobbyView(game, session, controller));
            }
        });

        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backTexture)));
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LogInView(game, session, controller));
            }
        });

        buttonRow.add(playAgain).size(432, 144).padRight(40);
        buttonRow.add(backButton).size(432, 144);
        root.add(buttonRow).colspan(2);
        root.row();


        Image logo = new Image(logoTexture);
        logo.setSize(864, 240);
        logo.setPosition(108, 1980);
        stage.addActor(logo);
    }

    private void setupVolumeToggle() {
        TextureRegion icon = new TextureRegion(game.isMuted() ? muteTexture : volumeTexture);
        volumeButton = new ImageButton(new TextureRegionDrawable(icon));
        volumeButton.setSize(100, 100);
        volumeButton.setPosition(stage.getViewport().getWorldWidth() - 110,
                stage.getViewport().getWorldHeight() - 110);
        volumeButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.toggleMute();
                TextureRegion newIcon = new TextureRegion(game.isMuted() ? muteTexture : volumeTexture);
                volumeButton.getStyle().imageUp = new TextureRegionDrawable(newIcon);
            }
        });
        stage.addActor(volumeButton);
    }

    private BitmapFont loadFont(String path, int size) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = Color.BLACK;
        BitmapFont f = gen.generateFont(param);
        gen.dispose();
        return f;
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(stage.getCamera().combined);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0,
                stage.getViewport().getWorldWidth(),
                stage.getViewport().getWorldHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        playAgainTexture.dispose();
        backTexture.dispose();
        volumeTexture.dispose();
        muteTexture.dispose();
        logoTexture.dispose();
        font.dispose();
        boldFont.dispose();
        skin.dispose();
    }
}
