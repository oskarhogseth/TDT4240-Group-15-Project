package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.controller.LobbyServiceInterface;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;
import group15.gdx.project.model.letters.LetterSet;

import java.util.Map;

public class LobbyView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private BitmapFont cinzelFont;

    private Texture backgroundTexture;
    private Texture startGameTexture;
    private Texture endGameTexture;
    private Texture leaveGameTexture;
    private Texture infoTexture;

    private Table playerListTable;

    public LobbyView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(1080, 2400));
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        startGameTexture = new Texture("startgame.png");
        endGameTexture = new Texture("endgame.png");
        leaveGameTexture = new Texture("leavegame.png");
        infoTexture = new Texture("info_button.png");

        skin = new Skin(Gdx.files.internal("vhs.json"));
        cinzelFont = loadCinzelFont(40);
        skin.get(Label.LabelStyle.class).font = cinzelFont;

        setupUI();
        startListeningForLobbyUpdates();
    }

    private void setupUI() {
        float screenWidth  = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        float baseFont     = screenHeight / 40f;

        stage.clear();

        Table root = new Table(skin);
        root.setFillParent(true);
        stage.addActor(root);

        // Title
        Label title = new Label("WAITING FOR OTHER PLAYERS", skin);
        title.setFontScale(1.2f);
        root.add(title).padTop(100).colspan(2).center();
        root.row();

        // Game PIN
        String pin = session.getLobby().getPin();
        Label pinLabel = new Label("Game PIN: " + pin, skin);
        pinLabel.setFontScale(baseFont / 20f);
        root.add(pinLabel).colspan(2).padBottom(screenHeight * 0.02f).center();
        root.row();

        // Players in lobby
        Label playersLabel = new Label("Players in lobby:", skin);
        playersLabel.setFontScale(baseFont / 22f);
        root.add(playersLabel).colspan(2).padBottom(screenHeight * 0.02f).center();
        root.row();

        // Player list
        playerListTable = new Table(skin);
        playerListTable.top();
        root.add(playerListTable).colspan(2).fillX();
        root.row();

        // Host-only buttons
        if (isHost()) {
            ImageButton startButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(startGameTexture)));
            startButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent ev,float x,float y) {
                    if (session.getLobby().getPlayers().isEmpty()) return;
                    LetterSet set = session.getGameController().generateLetters();
                    controller.startGame(session.getLobby().getPin(), set);
                }
            });

            ImageButton endButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(endGameTexture)));
            endButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent ev,float x,float y) {
                    controller.leaveLobby(session.getLobby().getPin(), session.getLocalPlayer().getUid());
                    game.setScreen(new LogInView(game, session, controller));
                }
            });

            root.add(startButton).size(220, 80).padTop(40).colspan(2).center();
            root.row();
            root.add(endButton).size(220, 80).padTop(20).colspan(2).center();
            root.row();
        } else {
            Label waitingMsg = new Label("Creator has to start game", skin);
            waitingMsg.setFontScale(0.9f);
            root.add(waitingMsg).padTop(50).colspan(2).center();
            root.row();

            ImageButton leaveBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(leaveGameTexture)));
            leaveBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    controller.leaveLobby(session.getLobby().getPin(), session.getLocalPlayer().getUid());
                    game.setScreen(new LogInView(game, session, controller));
                }
            });
            root.add(leaveBtn).size(220, 80).padTop(20).colspan(2).center();
            root.row();
        }

        // Info button overlay
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

    private void refreshPlayerList() {
        playerListTable.clearChildren();
        float baseFont = stage.getViewport().getWorldHeight() / 40f;

        for (Player p : session.getLobby().getPlayers()) {
            Label playerLabel = new Label(p.getNickname(), skin);
            playerLabel.setFontScale(baseFont / 24f);
            playerListTable.add(playerLabel).pad(5).center();
            playerListTable.row();
        }
    }

    private boolean isHost() {
        return session.getLocalPlayer().getUid()
                .equals(session.getLobby().getPlayers().get(0).getUid());
    }

    private void startListeningForLobbyUpdates() {
        String pin = session.getLobby().getPin();
        controller.listenToLobby(pin, new LobbyServiceInterface.PlayerUpdateCallback() {
            @Override
            public void onPlayersUpdated(Map<String, String> players) {
                session.getLobby().updatePlayersFromMap(players);
                Gdx.app.postRunnable(() -> refreshPlayerList());
            }

            @Override
            public void onGameStarted(LetterSet set) {
                session.getGameController().loadLetters(set);
                Gdx.app.postRunnable(() ->
                    game.setScreen(new GameView(game, session, session.getLocalPlayer()))
                );
            }
        });
    }

    private BitmapFont loadCinzelFont(int size) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("cinzel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = Color.BLACK;
        BitmapFont font = gen.generateFont(param);
        gen.dispose();
        return font;
    }

    @Override
    public void render(float delta) {
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
        startGameTexture.dispose();
        endGameTexture.dispose();
        leaveGameTexture.dispose();
        infoTexture.dispose();
        cinzelFont.dispose();
        skin.dispose();
    }
}
