package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
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
        logoTexture = new Texture("wordduel.png");
        joinButtonTexture = new Texture("joingame.png");
        backButtonTexture = new Texture("back.png");
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
        nicknameField.setPosition(180, 1650);
        stage.addActor(nicknameField);
        nicknameBox = new Rectangle(170, 1635, 740, 160);

        pinField = new TextField("", createTextFieldStyle());
        pinField.setMessageText("Enter PIN");
        pinField.setSize(720, 140);
        pinField.setPosition(180, 1330);
        stage.addActor(pinField);
        pinBox = new Rectangle(170, 1315, 740, 160);

        joinButtonRect = new Rectangle(324, 450, 432, 144);
        backButtonRect = new Rectangle(324, 200, 432, 144);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, 1080, 2400);
        batch.draw(logoTexture, 240, 1980, 600, 240);

        font.draw(batch, "ENTER NICKNAME", 180, 1845);
        batch.draw(whiteBox, nicknameBox.x, nicknameBox.y, nicknameBox.width, nicknameBox.height);

        font.draw(batch, "ENTER PIN", 180, 1545);
        batch.draw(whiteBox, pinBox.x, pinBox.y, pinBox.width, pinBox.height);

        batch.draw(joinButtonTexture, joinButtonRect.x, joinButtonRect.y, joinButtonRect.width, joinButtonRect.height);
        batch.draw(backButtonTexture, backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height);

        batch.end();

        stage.act(delta);
        stage.draw();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            stage.getViewport().unproject(touch);

            if (joinButtonRect.contains(touch)) {
                String pin = pinField.getText().trim();
                String nickname = nicknameField.getText().trim();
                if (!pin.isEmpty() && !nickname.isEmpty()) {
                    Player player = new Player("id-" + nickname, nickname);
                    session.setLocalPlayer(player);
                    session.getLobby().addPlayer(player);
                    game.setScreen(new LobbyView(game, session, controller));
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
        whiteBox.dispose();
        logoTexture.dispose();
    }
}
