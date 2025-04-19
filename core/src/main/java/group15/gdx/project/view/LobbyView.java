package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.controller.LobbyServiceInterface;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

import java.util.Map;

public class LobbyView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession gameSession;
    private final LobbyController controller;

    private final Stage stage;
    private final Skin skin;

    private Texture infoTexture;

    private static final String WELCOME_MESSAGE = "Welcome to the Lobby!";
    private static final String PLAYERS_IN_LOBBY = "Players in lobby:";
    private static final String START_GAME = "Play Single player";


    public LobbyView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.gameSession = session;
        this.controller = controller;

        infoTexture = new Texture(Gdx.files.internal("info_button.png"));

        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
        startListeningForLobbyUpdates();
    }

    private void setupUI() {
        float sw = stage.getViewport().getWorldWidth();
        float sh = stage.getViewport().getWorldHeight();
        float baseFont = sh / 40f;

        // ── ROOT LOBBY TABLE ──
        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(sh * 0.05f);
        stage.addActor(root);

        // Title
        Label titleLabel = new Label(WELCOME_MESSAGE, skin);
        titleLabel.setFontScale(baseFont / 20f);
        root.add(titleLabel).colspan(2).center();
        root.row().padTop(sh * 0.02f);

        // Players label
        Label playersLabel = new Label(PLAYERS_IN_LOBBY, skin);
        playersLabel.setFontScale(baseFont / 22f);
        root.add(playersLabel)
            .colspan(2)
            .center()
            .padBottom(sh * 0.02f);
        root.row();

        // Player list
        for (Player p : gameSession.getLobby().getPlayers()) {
            Label playerLabel = new Label(p.getNickname(), skin);
            playerLabel.setFontScale(baseFont / 24f);
            root.add(playerLabel)
                .colspan(2)
                .center()
                .pad(5);
            root.row();
        }

        // Spacer
        root.add().expandY().colspan(2);
        root.row();

        // Start button (host only)
        if (isHost()) {
            TextButton startButton = new TextButton(START_GAME, skin);
            startButton.getLabel().setFontScale(baseFont / 22f);
            startButton.setColor(0.8f, 0.2f, 0.2f, 1);
            startButton.addListener(evt -> {
                if (!startButton.isPressed()) return false;
                gameSession.getGameController().generateLetters();
                game.setScreen(new GameView(
                    game,
                    gameSession,
                    gameSession.getLobby().getPlayers().get(0)
                ));
                return true;
            });

            root.add(startButton)
                .width(sw * 0.5f)
                .height(sh * 0.08f)
                .padBottom(sh * 0.05f)
                .colspan(2)
                .center();
        }

        // ── INFO ICON OVERLAY ──
        Drawable infoDrawable = new TextureRegionDrawable(new TextureRegion(infoTexture));
        ImageButton infoButton = new ImageButton(infoDrawable);
        infoButton.addListener(evt -> {
            if (!infoButton.isPressed()) return false;
            game.setScreen(new HowToPlayView(game, gameSession, controller));
            return true;
        });

        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.top().right().pad(10);
        overlay.add(infoButton).size(sh * 0.04f);
        stage.addActor(overlay);
    }

    private boolean isHost() {
        return gameSession.getLocalPlayer().getUid()
            .equals(gameSession.getLobby().getPlayers().get(0).getUid());
    }

    private void startListeningForLobbyUpdates() {
        controller.listenToLobby(gameSession.getLobby().getPin(), new LobbyServiceInterface.PlayerUpdateCallback() {
            @Override
            public void onPlayersUpdated(Map<String, String> players) {
                gameSession.getLobby().updatePlayersFromMap(players);
                refreshPlayerList();
            }

            @Override
            public void onGameStarted() {
                game.setScreen(new GameView(game, gameSession, gameSession.getLocalPlayer()));
            }
        });
    }

    private void refreshPlayerList() {
        stage.clear();
        setupUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        infoTexture.dispose();
    }
}
