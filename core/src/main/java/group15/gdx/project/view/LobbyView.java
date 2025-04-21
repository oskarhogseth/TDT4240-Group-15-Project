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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private final GameSession session;
    private final LobbyController controller;

    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;

    private Texture backgroundTexture;
    private Texture startGameTexture;
    private Texture endGameTexture;
    private Texture leaveGameTexture;

    private BitmapFont cinzelFont;

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

        skin = new Skin(Gdx.files.internal("vhs.json"));
        cinzelFont = loadCinzelFont(40);
        skin.get(Label.LabelStyle.class).font = cinzelFont;

        setupUI();
        startListeningForLobbyUpdates();
    }

    private void setupUI() {
        stage.clear();
        float screenHeight = stage.getViewport().getWorldHeight();
        float screenWidth = stage.getViewport().getWorldWidth();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Title
        Label title = new Label("WAITING FOR OTHER PLAYERS", skin);
        title.setFontScale(1.2f);
        root.add(title).padTop(100).colspan(2).center();
        root.row();

        // PIN
        Label pinLabel = new Label("PIN: " + session.getLobby().getPin(), skin);
        pinLabel.setFontScale(1.0f);
        root.add(pinLabel).padTop(30).colspan(2).center();
        root.row();

        // Player list
        Table playerTable = new Table();
        int i = 0;
        for (Player p : session.getLobby().getPlayers()) {
            Label name = new Label(p.getNickname(), skin);
            name.setFontScale(0.9f);
            playerTable.add(name).pad(15).width(300);
            i++;
            if (i % 2 == 0) playerTable.row();
        }
        root.add(playerTable).padTop(30).colspan(2).center();
        root.row();

        // Message or buttons
        Table buttonTable = new Table();
        buttonTable.bottom().padBottom(100);

        if (isHost()) {
            ImageButton startBtn = makeButton(startGameTexture, () -> {
                if (!session.getLobby().getPlayers().isEmpty()) {
                    session.getGameController().generateLetters();
                    controller.startGame(session.getLobby().getPin());
                }
            });

            ImageButton endBtn = makeButton(endGameTexture, () -> {
                controller.leaveLobby(session.getLobby().getPin(), session.getLocalPlayer().getUid());
                game.setScreen(new LogInView(game, session, controller));
            });

            buttonTable.add(startBtn).pad(30);
            buttonTable.row();
            buttonTable.add(endBtn).pad(30);
        } else {
            Label waitingMsg = new Label("Creator has to start game", skin);
            waitingMsg.setFontScale(0.9f);
            root.add(waitingMsg).padTop(50).colspan(2).center();
            root.row();

            ImageButton leaveBtn = makeButton(leaveGameTexture, () -> {
                controller.leaveLobby(session.getLobby().getPin(), session.getLocalPlayer().getUid());
                game.setScreen(new LogInView(game, session, controller));
            });

            buttonTable.add(leaveBtn).pad(30);
        }

        root.add(buttonTable).expandY().bottom().colspan(2);
    }

    private ImageButton makeButton(Texture tex, Runnable onClick) {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(tex)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                onClick.run();
            }
        });
        return button;
    }

    private boolean isHost() {
        return session.getLocalPlayer().getUid()
                .equals(session.getLobby().getPlayers().get(0).getUid());
    }

    private void startListeningForLobbyUpdates() {
        controller.listenToLobby(session.getLobby().getPin(), new LobbyServiceInterface.PlayerUpdateCallback() {
            @Override
            public void onPlayersUpdated(Map<String, String> players) {
                session.getLobby().updatePlayersFromMap(players);
                setupUI();
            }

            @Override
            public void onGameStarted() {
                game.setScreen(new GameView(game, session, session.getLocalPlayer()));
            }
        });
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
        endGameTexture.dispose();
        leaveGameTexture.dispose();
        cinzelFont.dispose();
        skin.dispose();
    }
}
