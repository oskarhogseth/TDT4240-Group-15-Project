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
import group15.gdx.project.API;
import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;
import group15.gdx.project.model.Score;

import java.util.ArrayList;

public class Leaderboard extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;

    private BitmapFont font;
    private BitmapFont boldFont;

    private final Texture backgroundTexture;
    private final Texture logoTexture;
    private final Texture volumeTexture;
    private final Texture muteTexture;
    private final Texture backButtonTexture;

    private ImageButton volumeButton;

    private final Table leaderboardTable;
    private final ArrayList<Score> leaderboard = new ArrayList<>();

    private final API api;

    public Leaderboard(Launcher game, GameSession session) {
        this.game = game;
        this.session = session;
        this.stage = new Stage(new ScreenViewport());
        this.batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("vhs.json"));

        backgroundTexture = new Texture("background.png");
        logoTexture = new Texture("wordduel.png");
        volumeTexture = new Texture("volume.png");
        muteTexture = new Texture("mute.png");
        backButtonTexture = new Texture("back.png");

        font = loadFont("cinzel.ttf", 40);
        boldFont = loadFont("cinzel_bold.ttf", 52);
        skin.get(Label.LabelStyle.class).font = font;

        leaderboardTable = new Table(skin);
        leaderboardTable.top();

        api = game.getAPI();
        for (Player player : session.getLobby().getPlayers()) {
            api.submitScore(new Score(player.getName(), player.getScore()));
        }

        fetchLeaderboard();
        setupUI();
        setupVolumeToggle();
    }

    private void fetchLeaderboard() {
        api.getHighscores(leaderboard, () -> Gdx.app.postRunnable(this::populateLeaderboardTable));
    }

    private void setupUI() {
        float sw = stage.getViewport().getWorldWidth();
        float sh = stage.getViewport().getWorldHeight();

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(600); // ⬇ Move down to avoid logo
        stage.addActor(root);

        // Scoreboard title
        Label title = new Label("LEADERBOARD", new Label.LabelStyle(boldFont, Color.BLACK));
        title.setFontScale(1.8f);
        root.add(title).padBottom(40).center();
        root.row();

        // ScrollPane
        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        root.add(scrollPane).height(sh * 0.5f).width(sw * 0.8f);
        root.row();

        // Back Button
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backButtonTexture)));
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LogInView(game, session, game.getLobbyController()));
            }
        });
        root.add(backButton).size(300, 100).padTop(50);

        // Logo (same placement as other screens)
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

        int count = Math.min(leaderboard.size(), 50);
        for (int i = 0; i < count; i++) {
            Score s = leaderboard.get(i);
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
