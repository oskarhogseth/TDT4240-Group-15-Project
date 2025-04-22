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

public class HowToPlayView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private BitmapFont font;

    private Texture backgroundTexture;
    private Texture logoTexture;
    private Texture volumeTexture;
    private Texture muteTexture;
    private Texture backButtonTexture;
    private ImageButton volumeButton;

    private static final String HOW_TO_PLAY_TEXT =
            "• Guess as many words as possible in each round!\n\n" +
                    "• All possible words have between 3 to 7 characters\n\n" +
                    "• You can use the same letters multiple times\n\n";

    public HowToPlayView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(1080, 2400));
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        logoTexture = new Texture("wordduel.png");
        volumeTexture = new Texture("volume.png");
        muteTexture = new Texture("mute.png");
        backButtonTexture = new Texture("back.png");

        skin = new Skin(Gdx.files.internal("vhs.json"));
        font = loadCinzelFont(40);
        skin.get(Label.LabelStyle.class).font = font;

        setupUI();
        setupVolumeButton();
    }

    private void setupUI() {
        float sw = stage.getViewport().getWorldWidth();
        float sh = stage.getViewport().getWorldHeight();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Logo
        Image logo = new Image(logoTexture);
        logo.setSize(sw * 0.8f, sh * 0.2f);
        root.add(logo).colspan(2).padTop(150).padBottom(80).center();
        root.row();

        // Instructions
        Label instructions = new Label(HOW_TO_PLAY_TEXT, skin);
        instructions.setFontScale(0.9f);
        instructions.setWrap(true);
        root.add(instructions).width(sw * 0.8f).padBottom(100).center();
        root.row();

        // Back button
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backButtonTexture)));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LogInView(game, session, controller));
            }
        });
        root.add(backButton).size(400, 140).padBottom(50);
    }

    private void setupVolumeButton() {
        TextureRegionDrawable icon = new TextureRegionDrawable(new TextureRegion(
                game.isMuted() ? muteTexture : volumeTexture));
        volumeButton = new ImageButton(icon);
        volumeButton.setSize(100, 100);
        volumeButton.setPosition(stage.getViewport().getWorldWidth() - 110,
                stage.getViewport().getWorldHeight() - 110);
        volumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.toggleMute();
                TextureRegion iconTex = new TextureRegion(game.isMuted() ? muteTexture : volumeTexture);
                volumeButton.getStyle().imageUp = new TextureRegionDrawable(iconTex);
            }
        });
        stage.addActor(volumeButton);
    }

    private BitmapFont loadCinzelFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("cinzel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = Color.BLACK;
        BitmapFont font = generator.generateFont(param);
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
        volumeTexture.dispose();
        muteTexture.dispose();
        backButtonTexture.dispose();
        font.dispose();
        skin.dispose();
    }
}
