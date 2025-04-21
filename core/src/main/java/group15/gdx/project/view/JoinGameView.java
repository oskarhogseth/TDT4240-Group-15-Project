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
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

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
    private Texture whiteBox;
    private Texture logoTexture;
    private Texture volumeTexture;
    private Texture muteTexture;
    private ImageButton volumeButton;

    private TextField nicknameField;
    private TextField pinField;
    private Rectangle nicknameBox;
    private Rectangle pinBox;
    private Rectangle joinButtonRect;
    private Rectangle backButtonRect;

    private Label errorLabel;

    public JoinGameView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

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
        param.color = Color.GRAY;
        font = gen.generateFont(param);
        gen.dispose();

        nicknameField = new TextField("", createTextFieldStyle());
        nicknameField.setMessageText("Nickname");
        nicknameField.setSize(720, 120);
        nicknameField.setPosition(180, 1740);
        stage.addActor(nicknameField);
        nicknameBox = new Rectangle(170, 1735, 740, 120);

        pinField = new TextField("", createTextFieldStyle());
        pinField.setMessageText("PIN");
        pinField.setSize(720, 120);
        pinField.setPosition(180, 1440);
        stage.addActor(pinField);
        pinBox = new Rectangle(170, 1435, 740, 120);

        errorLabel = new Label("", new Label.LabelStyle(font, Color.RED));
        errorLabel.setFontScale(1f);
        errorLabel.setPosition(180, 700);
        stage.addActor(errorLabel);

        joinButtonRect = new Rectangle(324, 500, 432, 144);
        backButtonRect = new Rectangle(324, 250, 432, 144);

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

        font.draw(batch, "ENTER NICKNAME", 150, 1900);
        batch.draw(whiteBox, nicknameBox.x, nicknameBox.y, nicknameBox.width, nicknameBox.height);

        font.draw(batch, "ENTER PIN", 150, 1600);
        batch.draw(whiteBox, pinBox.x, pinBox.y, pinBox.width, pinBox.height);

        batch.draw(joinButtonTexture, joinButtonRect.x, joinButtonRect.y, joinButtonRect.width, joinButtonRect.height);
        batch.draw(backButtonTexture, backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height);

        font.setColor(Color.RED);
        if (!errorLabel.getText().toString().isEmpty()) {
            font.draw(batch, errorLabel.getText().toString(), 180, 650);
        }
        font.setColor(Color.GRAY);

        batch.end();

        handleInput();
        stage.act(delta);
        stage.draw();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            stage.getViewport().unproject(touch);

            if (joinButtonRect.contains(touch)) {
                String nickname = nicknameField.getText().trim();
                String pin = pinField.getText().trim();

                if (nickname.isEmpty()) {
                    errorLabel.setText("Please enter a nickname");
                } else if (pin.isEmpty() || pin.length() != 4) {
                    errorLabel.setText("Please enter a 4-digit PIN");
                } else {
                    Player player = new Player("id-" + nickname, nickname);
                    session.setLocalPlayer(player);
                    session.getLobby().setPin(pin);
                    controller.joinLobby(pin, nickname,
                            () -> game.setScreen(new LobbyView(game, session, controller)),
                            () -> errorLabel.setText("Failed to join lobby"));
                }
            }

            if (backButtonRect.contains(touch)) {
                game.setScreen(new LogInView(game, session, controller));
            }
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
