package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.controller.GameController;
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

    private Label pinLabel;

    private Texture infoTexture;

    private static final String WELCOME_MESSAGE = "Welcome to the Lobby!";
    private static final String PLAYERS_IN_LOBBY = "Players in lobby:";
    private static final String START_GAME = "Play Single player";

    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture startGameTexture;
    private BitmapFont cinzelFont;

    public LobbyView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        infoTexture = new Texture(Gdx.files.internal("info_button.png"));

        stage = new Stage(new FitViewport(1080, 2400));
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture("background.png");
        startGameTexture = new Texture("startgame.png");
        skin = new Skin(Gdx.files.internal("vhs.json"));
        cinzelFont = loadCinzelFont(28);

        skin.get(Label.LabelStyle.class).font = cinzelFont;


        if (session.getLobby().getPlayers().isEmpty()) {
            session.getLobby().addPlayer(new Player("Player1", "Charles"));
            session.getLobby().addPlayer(new Player("Player2", "Nick"));
        }

        setupUI();
        startListeningForLobbyUpdates();
    }

    private void setupUI() {
        float screenWidth  = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        float baseFont     = screenHeight / 40f;

        // ── ROOT LOBBY TABLE ──
        Table root = new Table(skin);
        root.setFillParent(true);
        root.top().padTop(screenHeight * 0.03f);
        stage.addActor(root);

        // Title
        Label title = new Label(WELCOME_MESSAGE, skin);
        title.setColor(0, 0, 0, 1);
        title.setFontScale(2f);
        root.add(title).colspan(2).padBottom(20).center();
        root.row();

        // Game PIN
        String pin = session.getLobby().getPin();
        Label pinLabel = new Label("Game PIN: " + pin, skin);
        pinLabel.setFontScale(baseFont / 20f);
        root.add(pinLabel).colspan(2).padBottom(screenHeight * 0.02f).center();
        root.row();

        // “Players in Lobby:” header
        Label playersLabel = new Label(PLAYERS_IN_LOBBY, skin);
        playersLabel.setFontScale(baseFont / 22f);
        root.add(playersLabel).colspan(2).padBottom(screenHeight * 0.02f).center();
        root.row();

        // Dynamic player list table (starts empty, filled by refreshPlayerList)
        playerListTable = new Table(skin);
        playerListTable.top();
        root.add(playerListTable).colspan(2).fillX();
        root.row();

        // Host‑only “Start Game” button
        if (isHost()) {
            ImageButton startButton = new ImageButton(
                new TextureRegionDrawable(new TextureRegion(startGameTexture)));
            startButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent ev,float x,float y) {
                    if (session.getLobby().getPlayers().isEmpty()) return;

                    LetterSet set = session.getGameController().generateLetters();
                    String pin   = session.getLobby().getPin();
                    controller.startGame(pin, set);
                }
            });
            root.add(startButton)
                .size(220, 80)
                .padTop(40)
                .colspan(2)
                .center();
            root.row();
        }

        // ── INFO ICON OVERLAY ──
        Drawable infoDrawable = new TextureRegionDrawable(new TextureRegion(infoTexture));
        ImageButton infoButton = new ImageButton(infoDrawable);
        infoButton.addListener(evt -> {
            if (!infoButton.isPressed()) return false;
            game.setScreen(new HowToPlayView(game, session, controller));
            return true;
        });
        Table overlay = new Table(skin);
        overlay.setFillParent(true);
        overlay.top().right().pad(10);
        overlay.add(infoButton).size(screenHeight * 0.04f);
        stage.addActor(overlay);
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
    private boolean isHost() {
        return session.getLocalPlayer().getUid()
            .equals(session.getLobby().getPlayers().get(0).getUid());
    }

    private void startListeningForLobbyUpdates() {
        String pin = session.getLobby().getPin();
        controller.listenToLobby(pin, new LobbyServiceInterface.PlayerUpdateCallback() {

            @Override
            public void onPlayersUpdated(Map<String,String> players) {
                session.getLobby().updatePlayersFromMap(players);
                Gdx.app.postRunnable(() -> refreshPlayerList());
            }

            @Override
            public void onGameStarted(LetterSet set) {
                session.getGameController().loadLetters(set);   // sync letters
                Gdx.app.postRunnable(() ->
                    game.setScreen(new GameView(
                        game, session, session.getLocalPlayer()))
                );
            }
        });
    }

    private void refreshPlayerList() {
        // clear only the player rows
        playerListTable.clearChildren();

        float screenHeight = stage.getViewport().getWorldHeight();
        float baseFont     = screenHeight / 40f;

        for (Player p : session.getLobby().getPlayers()) {
            Label name = new Label(p.getNickname(), skin);
            name.setFontScale(baseFont / 24f);
            playerListTable.add(name).pad(5).center();
            playerListTable.row();
        }
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
        skin.dispose();
        infoTexture.dispose();
    }
}
