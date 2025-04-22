package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.controller.LobbyServiceInterface;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.LetterSet;
import group15.gdx.project.model.Player;

import java.util.Map;

public class LobbyView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    private final Stage stage;
    private final Skin skin;

    private Table playerListTable;
    private SpriteBatch batch;

    private Texture backgroundTexture;
    private Texture startGameTexture;
    private Texture leaveGameTexture;
    private Texture endGameTexture;

    private BitmapFont cinzelFont;

    private static final String WELCOME_MESSAGE = "Welcome to the Lobby!";
    private static final String PLAYERS_IN_LOBBY = "Players in lobby:";

    public LobbyView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(1080, 2400));
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        startGameTexture = new Texture("startgame.png");
        leaveGameTexture = new Texture("leavegame.png");
        endGameTexture = new Texture("endgame.png");

        skin = new Skin(Gdx.files.internal("vhs.json"));
        cinzelFont = loadCinzelFont(48);
        skin.get(Label.LabelStyle.class).font = cinzelFont;

        setupUI();
        startListeningForLobbyUpdates();
    }

    private void setupUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table content = new Table();
        content.top().padTop(80).padLeft(60).padRight(60);
        content.defaults().padBottom(40);

        // Title
        Label title = new Label(WELCOME_MESSAGE, skin);
        title.setColor(0, 0, 0, 1);
        title.setFontScale(1.8f);
        content.add(title).center().colspan(2);
        content.row();

        // Game PIN
        Label pinLabel = new Label("Game PIN: " + session.getLobby().getPin(), skin);
        pinLabel.setFontScale(1.7f);
        content.add(pinLabel).center().colspan(2);
        content.row();

        // Players header
        Label playersLabel = new Label(PLAYERS_IN_LOBBY, skin);
        playersLabel.setFontScale(1.5f);
        content.add(playersLabel).center().colspan(2);
        content.row();

        // Player list
        playerListTable = new Table();
        content.add(playerListTable).fillX().center().colspan(2);
        content.row();

        root.add(content).expand().fill();
        root.row();

        // Bottom sticky buttons
        Table bottomBar = new Table();
        bottomBar.bottom().padBottom(60);

        if (isHost()) {
            ImageButton startButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(startGameTexture)));
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent ev, float x, float y) {
                    if (session.getLobby().getPlayers().isEmpty()) return;
                    LetterSet set = session.getGameController().generateLetters();
                    controller.startGame(session.getLobby().getPin(), set);
                }
            });

            ImageButton endButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(endGameTexture)));
            endButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent ev, float x, float y) {
                    controller.leaveGame(session.getLobby().getPin(), session.getLocalPlayer().getId(), () -> {
                        Gdx.app.postRunnable(() ->
                                game.setScreen(new LogInView(game, session, controller)));
                    });
                }
            });

            bottomBar.add(startButton).size(400, 160).padRight(40);
            bottomBar.add(endButton).size(400, 160);
        } else {
            ImageButton leaveButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(leaveGameTexture)));
            leaveButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent ev, float x, float y) {
                    controller.leaveGame(session.getLobby().getPin(), session.getLocalPlayer().getId(), () -> {
                        Gdx.app.postRunnable(() ->
                                game.setScreen(new LogInView(game, session, controller)));
                    });
                }
            });
            bottomBar.add(leaveButton).size(400, 160).colspan(2);
        }

        root.add(bottomBar).fillX().bottom().padBottom(60);
    }

    private void startListeningForLobbyUpdates() {
        controller.listenToLobby(session.getLobby().getPin(), new LobbyServiceInterface.PlayerUpdateCallback() {
            @Override
            public void onPlayersUpdated(Map<String, String> players) {
                session.getLobby().updatePlayersFromMap(players);
                Gdx.app.postRunnable(() -> refreshPlayerList());
            }

            @Override
            public void onGameStarted(LetterSet set) {
                session.getGameController().loadLetters(set);
                Gdx.app.postRunnable(() ->
                        game.setScreen(new GameView(game, session, session.getLocalPlayer())));
            }
        });
    }

    private void refreshPlayerList() {
        playerListTable.clearChildren();
        for (Player p : session.getLobby().getPlayers()) {
            Label name = new Label(p.getNickname(), skin);
            name.setFontScale(1.4f);
            playerListTable.add(name).padBottom(20).center();
            playerListTable.row();
        }
    }

    private boolean isHost() {
        return session.getLocalPlayer().getUid()
                .equals(session.getLobby().getPlayers().get(0).getUid());
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
        Gdx.gl.glClearColor(0.95f, 0.90f, 0.80f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.draw(
                backgroundTexture,
                0, 0,
                stage.getViewport().getWorldWidth(),
                stage.getViewport().getWorldHeight()
        );
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
        leaveGameTexture.dispose();
        endGameTexture.dispose();
        cinzelFont.dispose();
        skin.dispose();
    }
}
