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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LeaderboardController;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Score;

public class LeaderboardView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LeaderboardController controller;

    private final Stage stage;
    private final SpriteBatch batch;
    private final Skin skin;

    private BitmapFont font, boldFont;
    private final Table leaderboardTable = new Table();
    private Texture backgroundTexture, logoTexture, volumeTexture, muteTexture, backButtonTexture;
    private ImageButton volumeButton;

    public LeaderboardView(Launcher game, GameSession session, LeaderboardController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("vhs.json"));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture("background.png");
        logoTexture = new Texture("wordduel.png");
        volumeTexture = new Texture("volume.png");
        muteTexture = new Texture("mute.png");
        backButtonTexture = new Texture("back.png");

        font = loadFont("cinzel.ttf", 40);
        boldFont = loadFont("cinzel_bold.ttf", 52);
        skin.get(Label.LabelStyle.class).font = font;

        setupUI();
        setupVolumeToggle();

        controller.fetchScores(this::populateLeaderboardTable);
    }

    private void setupUI() {
        float sw = stage.getViewport().getWorldWidth();
        float sh = stage.getViewport().getWorldHeight();

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(600);
        stage.addActor(root);

        Label title = new Label("LEADERBOARD", new Label.LabelStyle(boldFont, Color.BLACK));
        title.setFontScale(1.8f);
        root.add(title).padBottom(40).center();
        root.row();

        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        root.add(scrollPane).height(sh * 0.5f).width(sw * 0.8f);
        root.row();

        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backButtonTexture)));
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LogInView(game, session, game.getLobbyController()));
            }
        });
        root.add(backButton).size(300, 100).padTop(50);

        Image logo = new Image(logoTexture);
        logo.setSize(864, 240);
        logo.setPosition(108, 1980);
        stage.addActor(logo);
    }

    private void populateLeaderboardTable() {
        leaderboardTable.clear();

        Label.LabelStyle headerStyle = new Label.LabelStyle(font, Color.BLACK);
        leaderboardTable.add(new Label("Rank", headerStyle)).width(120).align(Align.center);
        leaderboardTable.add(new Label("Player", headerStyle)).width(300).align(Align.left);
        leaderboardTable.add(new Label("Score", headerStyle)).width(120).align(Align.center);
        leaderboardTable.row();

        int count = Math.min(controller.getScores().size(), 50);
        for (int i = 0; i < count; i++) {
            Score s = controller.getScores().get(i);
            leaderboardTable.add(new Label(String.valueOf(i + 1), skin)).align(Align.center);
            leaderboardTable.add(new Label(s.getPlayer(), skin)).align(Align.left);
            leaderboardTable.add(new Label(String.valueOf(s.getScore()), skin)).align(Align.center);
            leaderboardTable.row();
        }
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

    private BitmapFont loadFont(String filename, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(filename));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = Color.BLACK;
        BitmapFont f = generator.generateFont(param);
        generator.dispose();
        return f;
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
        batch.dispose();
        stage.dispose();
        font.dispose();
        boldFont.dispose();
        backgroundTexture.dispose();
        logoTexture.dispose();
        volumeTexture.dispose();
        muteTexture.dispose();
        backButtonTexture.dispose();
        skin.dispose();
    }
}
