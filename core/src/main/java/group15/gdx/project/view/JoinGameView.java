package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Map;

import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.controller.LobbyServiceInterface;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

public class JoinGameView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    Skin uiSkin = new Skin(Gdx.files.internal("vhs.json"));
    Dialog dialog = new Dialog("Error", uiSkin);

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

    private TextField pinField;
    private TextField nicknameField;
    private Rectangle joinButtonRect;
    private Rectangle backButtonRect;
    private Rectangle pinBox;
    private Rectangle nicknameBox;

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
        param.color = Color.BLACK;
        font = gen.generateFont(param);
        gen.dispose();

        nicknameField = new TextField("", createTextFieldStyle());
        nicknameField.setMessageText("Nickname");
        nicknameField.setSize(720, 140);
        nicknameField.setPosition(180, 1600); // moved down a bit
        stage.addActor(nicknameField);
        nicknameBox = new Rectangle(170, 1585, 740, 160);

        pinField = new TextField("", createTextFieldStyle());
        pinField.setMessageText("PIN");
        pinField.setSize(720, 140);
        pinField.setPosition(180, 1280); // moved down a bit
        stage.addActor(pinField);
        pinBox = new Rectangle(170, 1265, 740, 160);

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
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
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

        // draw logo the same size and placement as in LogInView/CreateGameView
        batch.draw(logoTexture, 108, 1980, 864, 240); // 80% of width, centered

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

        // JOIN GAME
        if (joinButtonRect.contains(touch)) {
            String pin      = pinField.getText().trim();
            String nickname = nicknameField.getText().trim();
            if (pin.isEmpty() || nickname.isEmpty()) return;

            // Create and store our local player
            Player player = new Player("id-" + nickname, nickname);
            session.setLocalPlayer(player);

            // Attempt to join via Firebase
            controller.joinLobby(pin, nickname, new LobbyServiceInterface.JoinCallback() {
                @Override
                public void onSuccess() {
                    Gdx.app.postRunnable(() -> {
                        // Record the lobby PIN we just joined
                        session.getLobby().setPin(pin);
                        // Optionally seed our own entry until real update arrives
                        session.getLobby().updatePlayersFromMap(
                            Map.of(player.getId(), player.getNickname())
                        );
                        // Navigate into the shared LobbyView
                        game.setScreen(new LobbyView(game, session, controller));
                    });
                }

                @Override
                public void onError(String message) {
                    Gdx.app.postRunnable(() ->
                        showAlert("Failed to join lobby: " + message)
                    );
                }
            });
        }

        // BACK TO LOGIN
        if (backButtonRect.contains(touch)) {
            game.setScreen(new LogInView(game, session, controller));
        }
    }

    private void showAlert(String message) {
        Dialog dialog = new Dialog("Error", uiSkin);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
        Label msgLabel = new Label(message, labelStyle);
        msgLabel.setWrap(true);
        dialog.getContentTable()
            .pad(20)
            .add(msgLabel)
            .width(stage.getViewport().getWorldWidth() * 0.8f)
            .row();
        dialog.button("OK", true).padTop(10);
        dialog.pack();
        dialog.show(stage);
        // shift down if needed
        float x = (stage.getViewport().getWorldWidth()  - dialog.getWidth())  / 2f;
        float y = (stage.getViewport().getWorldHeight() - dialog.getHeight()) / 2f - 150f;
        dialog.setPosition(x, y);
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
