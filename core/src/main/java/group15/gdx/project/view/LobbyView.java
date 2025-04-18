package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

public class LobbyView extends ScreenAdapter {
    private final Launcher game;
    private final GameSession session;

    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture startGameTexture;
    private Skin skin;
    private BitmapFont cinzelFont;

    public LobbyView(Launcher game, GameSession session) {
        this.game = game;
        this.session = session;

        stage = new Stage(new FitViewport(480, 800));
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture("background.png");
        startGameTexture = new Texture("startgame.png");
        skin = new Skin(Gdx.files.internal("vhs.json"));
        cinzelFont = loadCinzelFont(28);

        skin.get(Label.LabelStyle.class).font = cinzelFont;


        if (session.getLobby().getPlayers().isEmpty()) {
            session.getLobby().addPlayer(new Player("Player1"));
            session.getLobby().addPlayer(new Player("Player2"));
        }

        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(screenHeight * 0.03f);
        stage.addActor(root);

        Label title = new Label("Lobby", skin);
        title.setColor(0, 0, 0, 1);
        title.setFontScale(2f);
        root.add(title).colspan(2).padBottom(20).center();
        root.row();

        for (Player p : session.getLobby().getPlayers()) {
            Label playerLabel = new Label(p.getName(), skin);
            playerLabel.setColor(0, 0, 0, 1);
            root.add(playerLabel).colspan(2).pad(5).center();
            root.row();
        }

        ImageButton startButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(startGameTexture)));
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (session.getLobby().getPlayers().isEmpty()) {
                    System.out.println(" No players in the lobby.");
                    return;
                }

                session.getGameController().generateLetters();
                game.setScreen(new GameView(game, session, session.getLobby().getPlayers().get(0)));
            }
        });

        root.add(startButton).size(220, 80).padTop(40).colspan(2).center();
    }

    private BitmapFont loadCinzelFont(int size) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("cinzel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = com.badlogic.gdx.graphics.Color.BLACK;
        BitmapFont font = gen.generateFont(param);
        gen.dispose();
        return font;
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        startGameTexture.dispose();
        cinzelFont.dispose();
    }
}
