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

public class CreateGameView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;
    private Texture createButtonTexture;
    private Texture backButtonTexture;
    private Texture whiteBox;
    private Texture logoTexture;

    private Texture threeBronze, fiveBronze, sevenBronze;
    private Texture threeYellow, fiveYellow, sevenYellow;
    private Texture normalBronze, hardBronze;
    private Texture normalYellow, hardYellow;

    private Rectangle nicknameBox;
    private TextField nicknameField;
    private Rectangle createButtonRect;
    private Rectangle backButtonRect;

    private Rectangle[] roundRects;
    private Rectangle[] difficultyRects;

    private final String[] roundOptions = {"3", "5", "7"};
    private final String[] difficultyOptions = {"normal", "hard"};

    private String selectedRounds = "3";
    private String selectedDifficulty = "normal";

    public CreateGameView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        createButtonTexture = new Texture("creategame.png");
        backButtonTexture = new Texture("back.png");
        logoTexture = new Texture("wordduel.png");

        whiteBox = createSolidColorTexture(Color.WHITE);

        threeBronze = new Texture("threebronze.png");
        fiveBronze = new Texture("fivebronze.png");
        sevenBronze = new Texture("sevenbronze.png");
        threeYellow = new Texture("threeyellow.png");
        fiveYellow = new Texture("fiveyellow.png");
        sevenYellow = new Texture("sevenyellow.png");

        normalBronze = new Texture("normalbronze.png");
        hardBronze = new Texture("hardbronze.png");
        normalYellow = new Texture("normalyellow.png");
        hardYellow = new Texture("hardyellow.png");

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("cinzel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 54;
        param.color = Color.GRAY;
        font = gen.generateFont(param);
        gen.dispose();

        nicknameField = new TextField("", createTextFieldStyle());
        nicknameField.setMessageText("Nickname");
        nicknameField.setSize(720, 140);
        nicknameField.setPosition(180, 1810);
        stage.addActor(nicknameField);
        nicknameBox = new Rectangle(170, 1795, 740, 160);

        roundRects = new Rectangle[3];
        difficultyRects = new Rectangle[2];
        for (int i = 0; i < 3; i++) {
            roundRects[i] = new Rectangle(130 + i * 270, 1420, 240, 130);
        }
        for (int i = 0; i < 2; i++) {
            difficultyRects[i] = new Rectangle(210 + i * 330, 1120, 300, 130);
        }

        createButtonRect = new Rectangle(324, 450, 432, 144);
        backButtonRect = new Rectangle(324, 200, 432, 144);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, 1080, 2400);

        // Logo at top center
        batch.draw(logoTexture, 190, 2100, 700, 180);

        font.draw(batch, "ENTER NICKNAME", 180, 2010); // moved 50px down
        batch.draw(whiteBox, nicknameBox.x, nicknameBox.y, nicknameBox.width, nicknameBox.height);

        font.draw(batch, "SELECT ROUNDS", 150, 1610); // moved 50px down
        drawButton(batch, roundRects[0], selectedRounds.equals("3") ? threeYellow : threeBronze);
        drawButton(batch, roundRects[1], selectedRounds.equals("5") ? fiveYellow : fiveBronze);
        drawButton(batch, roundRects[2], selectedRounds.equals("7") ? sevenYellow : sevenBronze);

        font.draw(batch, "SELECT DIFFICULTY", 150, 1310); // moved 50px down
        drawButton(batch, difficultyRects[0], selectedDifficulty.equals("normal") ? normalYellow : normalBronze);
        drawButton(batch, difficultyRects[1], selectedDifficulty.equals("hard") ? hardYellow : hardBronze);

        batch.draw(createButtonTexture, createButtonRect.x, createButtonRect.y, createButtonRect.width, createButtonRect.height);
        batch.draw(backButtonTexture, backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height);

        batch.end();
        stage.act(delta);
        stage.draw();

        handleInput();
    }

    private void drawButton(SpriteBatch batch, Rectangle rect, Texture texture) {
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            stage.getViewport().unproject(touch);

            for (int i = 0; i < roundRects.length; i++) {
                if (roundRects[i].contains(touch)) {
                    selectedRounds = roundOptions[i];
                }
            }

            for (int i = 0; i < difficultyRects.length; i++) {
                if (difficultyRects[i].contains(touch)) {
                    selectedDifficulty = difficultyOptions[i];
                }
            }

            if (createButtonRect.contains(touch)) {
                String nickname = nicknameField.getText().trim();
                if (!nickname.isEmpty()) {
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
        createButtonTexture.dispose();
        backButtonTexture.dispose();
        logoTexture.dispose();
        whiteBox.dispose();
        threeBronze.dispose(); fiveBronze.dispose(); sevenBronze.dispose();
        threeYellow.dispose(); fiveYellow.dispose(); sevenYellow.dispose();
        normalBronze.dispose(); hardBronze.dispose();
        normalYellow.dispose(); hardYellow.dispose();
    }
}
