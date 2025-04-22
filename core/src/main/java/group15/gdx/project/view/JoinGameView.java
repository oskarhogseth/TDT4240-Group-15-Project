package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.controller.LobbyServiceInterface;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;
import group15.gdx.project.controller.GameController;
import group15.gdx.project.controller.LeaderboardController;
import group15.gdx.project.controller.LobbyController;

import java.util.Map;

public class JoinGameView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;

    private Texture backgroundTexture;
    private Texture joinButtonTexture;
    private Texture backButtonTexture;
    private Texture logoTexture;
    private Texture volumeTexture;
    private Texture muteTexture;
    private Texture whiteBox;

    private ImageButton volumeButton;

    private TextField pinField;
    private TextField nicknameField;
    private Rectangle joinButtonRect;
    private Rectangle backButtonRect;
    private Rectangle pinBox;
    private Rectangle nicknameBox;

    private Label errorLabel;

    private final GameController gameController;
    private final LeaderboardController leaderboardController;

    public JoinGameView(Launcher game, GameSession session, LobbyController controller, LeaderboardController leaderboardController, GameController gameController) {
        this.game = game;
        this.session = session;
        this.controller = controller;
        this.leaderboardController = leaderboardController;
        this.gameController = gameController;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        joinButtonTexture = new Texture("joingame.png");
        backButtonTexture = new Texture("back.png");
        logoTexture = new Texture("wordduel.png");
        volumeTexture = new Texture("volume.png");
        muteTexture = new Texture("mute.png");

        whiteBox = createSolidColorTexture(Color.WHITE);

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("cinzel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 54;
        param.color = Color.BLACK;
        font = gen.generateFont(param);
        gen.dispose();

        nicknameField = new TextField("", createTextFieldStyle());
        nicknameField.setMessageText("Nickname");
        nicknameField.setSize(720, 140);
        nicknameField.setPosition(180, 1600);
        stage.addActor(nicknameField);
        nicknameBox = new Rectangle(170, 1585, 740, 160);

        pinField = new TextField("", createTextFieldStyle());
        pinField.setMessageText("PIN");
        pinField.setSize(720, 140);
        pinField.setPosition(180, 1280);
        stage.addActor(pinField);
        pinBox = new Rectangle(170, 1265, 740, 160);

        errorLabel = new Label("", new Label.LabelStyle(font, Color.RED));
        errorLabel.setFontScale(0.9f);
        errorLabel.setPosition(180, 1200);
        stage.addActor(errorLabel);

        joinButtonRect = new Rectangle(324, 450, 432, 144);
        backButtonRect = new Rectangle(324, 200, 432, 144);

        setupVolumeButton();
    }

    private void setupVolumeButton() {
        TextureRegionDrawable iconDrawable = new TextureRegionDrawable(
                new TextureRegion(game.isMuted() ? muteTexture : volumeTexture));
        volumeButton = new ImageButton(iconDrawable);
        volumeButton.setSize(100, 100);
        volumeButton.setPosition(stage.getViewport().getWorldWidth() - 110, stage.getViewport().getWorldHeight() - 110);
        volumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.toggleMute();
                TextureRegion region = new TextureRegion(game.isMuted() ? muteTexture : volumeTexture);
                volumeButton.getStyle().imageUp = new TextureRegionDrawable(region);
            }
        });
        stage.addActor(volumeButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, 1080, 2400);
        batch.draw(logoTexture, 108, 1980, 864, 240);
        font.draw(batch, "ENTER NICKNAME", 180, 1800);
        batch.draw(whiteBox, nicknameBox.x, nicknameBox.y, nicknameBox.width, nicknameBox.height);
        font.draw(batch, "ENTER PIN", 180, 1480);
        batch.draw(whiteBox, pinBox.x, pinBox.y, pinBox.width, pinBox.height);
        batch.draw(joinButtonTexture, joinButtonRect.x, joinButtonRect.y, joinButtonRect.width, joinButtonRect.height);
        batch.draw(backButtonTexture, backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height);
        batch.end();

        stage.act(delta);
        stage.draw();

        handleInput();
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) return;

        Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        stage.getViewport().unproject(touch);

        if (joinButtonRect.contains(touch)) {
            String pin = pinField.getText().trim();
            String nickname = nicknameField.getText().trim();

            if (nickname.isEmpty()) {
                errorLabel.setText("Please enter a nickname.");
                return;
            }

            if (pin.length() != 4 || !pin.matches("\\d+")) {
                errorLabel.setText("PIN must be a 4-digit number.");
                return;
            }

            Player player = new Player("id-" + nickname, nickname);
            session.setLocalPlayer(player);

            controller.joinLobby(pin, nickname, new LobbyServiceInterface.JoinCallback() {
                @Override
                public void onSuccess() {
                    Gdx.app.postRunnable(() -> {
                        session.getLobby().setPin(pin);
                        session.getLobby().updatePlayersFromMap(Map.of(player.getId(), player.getNickname()));
                        game.setScreen(new LobbyView(game, session, controller, gameController, leaderboardController));
                    });
                }

                @Override
                public void onError(String message) {
                    Gdx.app.postRunnable(() -> errorLabel.setText("Failed to join: " + message));
                }
            });
        }

        if (backButtonRect.contains(touch)) {
            game.setScreen(new LogInView(game, session, controller, gameController, leaderboardController));
        }
    }

    private TextField.TextFieldStyle createTextFieldStyle() {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        return style;
    }

    private Texture createSolidColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose();
        joinButtonTexture.dispose();
        backButtonTexture.dispose();
        logoTexture.dispose();
        volumeTexture.dispose();
        muteTexture.dispose();
        whiteBox.dispose();
    }
}
