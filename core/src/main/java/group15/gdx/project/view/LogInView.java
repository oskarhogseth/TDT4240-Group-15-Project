package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.model.GameSession;

public class LogInView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private BitmapFont cinzelFont;

    private Texture backgroundTexture;
    private Texture logoTexture;
    private Texture createTexture;
    private Texture joinTexture;
    private Texture leaderboardTexture;
    private Texture howToPlayTexture;
    private Texture volumeTexture;
    private Texture muteTexture;
    private ImageButton volumeButton;

    public LogInView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(1080, 2400));
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        logoTexture = new Texture("wordduel.png");
        createTexture = new Texture("createnewgame.png");
        joinTexture = new Texture("joingame1.png");
        leaderboardTexture = new Texture("scoreboard.png");
        howToPlayTexture = new Texture("howtoplay.png");
        volumeTexture = new Texture("volume.png");
        muteTexture = new Texture("mute.png");

        skin = new Skin(Gdx.files.internal("vhs.json"));
        cinzelFont = loadCinzelFont(30);
        skin.get(Label.LabelStyle.class).font = cinzelFont;

        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Logo
        Image logo = new Image(logoTexture);
        logo.setSize(screenWidth * 0.8f, screenHeight * 0.2f);
        root.add(logo).colspan(2).padTop(150).padBottom(80).center();
        root.row();

        // Buttons column
        Table buttonCol = new Table();
        float spacing = 80f;

        buttonCol.add(makeMenuButton(createTexture, () -> game.setScreen(new CreateGameView(game, session, controller))))
                .padBottom(spacing);
        buttonCol.row();

        buttonCol.add(makeMenuButton(joinTexture, () -> game.setScreen(new JoinGameView(game, session, controller))))
                .padBottom(spacing);
        buttonCol.row();

        buttonCol.add(makeMenuButton(leaderboardTexture, () -> game.setScreen(new Leaderboard(game, session))))
                .padBottom(spacing);
        buttonCol.row();

        buttonCol.add(makeMenuButton(howToPlayTexture, () -> {
            game.setScreen(new HowToPlayView(game, session, controller));
        }));

        root.add(buttonCol).center().expand();

        // Volume toggle icon (top-right)
        TextureRegionDrawable iconDrawable = new TextureRegionDrawable(
                new TextureRegion(game.isMuted() ? muteTexture : volumeTexture));
        volumeButton = new ImageButton(iconDrawable);
        volumeButton.setSize(100, 100);
        volumeButton.setPosition(screenWidth - 110, screenHeight - 110);
        volumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.toggleMute();
                TextureRegion region = new TextureRegion(game.isMuted() ? muteTexture : volumeTexture);
                volumeButton.getStyle().imageUp = new TextureRegionDrawable(region);
            }
        });
        stage.addActor(volumeButton);
    }

    private ImageButton makeMenuButton(Texture texture, Runnable onClick) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        drawable.setMinWidth(600);
        drawable.setMinHeight(200);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = drawable;

        ImageButton button = new ImageButton(style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
        });
        return button;
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

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        logoTexture.dispose();
        createTexture.dispose();
        joinTexture.dispose();
        leaderboardTexture.dispose();
        howToPlayTexture.dispose();
        volumeTexture.dispose();
        muteTexture.dispose();
        cinzelFont.dispose();
        skin.dispose();
    }
}
