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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.model.GameSession;

public class HowToPlayView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession gameSession;
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

    public HowToPlayView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.gameSession = session;
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
        font = loadCinzelFont(36);
        skin.get(Label.LabelStyle.class).font = font;

        setupUI();
        setupVolumeButton();
    }

    private void setupUI() {
        float sw = stage.getViewport().getWorldWidth();
        float sh = stage.getViewport().getWorldHeight();

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(sh * 0.05f);
        stage.addActor(root);

        // Logo at top
        Image logo = new Image(logoTexture);
        logo.setSize(sw * 0.8f, sh * 0.1f);
        root.add(logo).colspan(2).padBottom(sh * 0.05f).center();
        root.row();

        // Instruction lines
        String[] lines = new String[]{
                "HOW TO PLAY",
                "",
                "1. Create or join a lobby using a game PIN and nickname.",
                "2. Guess as many valid words as you can in each round.",
                "3. Words must be between 3 and 7 letters long, and in the dictionary.",
                "4. Letters can be reused more than once.",
                "5. When the timer runs out, you'll see the round results.",
                "6. The player with the highest score at the end wins!"
        };

        for (int i = 0; i < lines.length; i++) {
            Label line;
            if (i == 0) {
                Label.LabelStyle boldStyle = new Label.LabelStyle(loadCinzelBoldFont(50), Color.BLACK);
                line = new Label(lines[i], boldStyle);
                line.setFontScale(1.6f);
            } else {
                line = new Label(lines[i], skin);
                line.setFontScale(1.2f);
            }
            line.setAlignment(Align.center);
            line.setWrap(true);
            root.add(line).width(sw * 0.85f).padBottom(i == 0 ? sh * 0.03f : sh * 0.02f);
            root.row();
        }

        // Back button at bottom
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backButtonTexture)));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LogInView(game, gameSession, controller));
            }
        });

        Table bottom = new Table();
        bottom.setFillParent(true);
        bottom.bottom().padBottom(sh * 0.04f);
        bottom.add(backButton).size(sw * 0.4f, sh * 0.06f);
        stage.addActor(bottom);
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
                TextureRegion newIcon = new TextureRegion(game.isMuted() ? muteTexture : volumeTexture);
                volumeButton.getStyle().imageUp = new TextureRegionDrawable(newIcon);
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

    private BitmapFont loadCinzelBoldFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("cinzel_bold.ttf"));
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

