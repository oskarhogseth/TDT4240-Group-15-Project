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


import java.util.Map;

public class CreateGameView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;
    private BitmapFont smallFont;


    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;
    private Texture createButtonTexture;
    private Texture backButtonTexture;
    private Texture whiteBox;
    private Texture logoTexture;
    private Texture volumeTexture;
    private Texture muteTexture;
    private ImageButton volumeButton;
    private Label errorLabel;


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

    private Skin uiSkin = new Skin(Gdx.files.internal("vhs.json"));

    public CreateGameView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        // Load textures
        backgroundTexture = new Texture("background.png");
        createButtonTexture = new Texture("creategame.png");
        backButtonTexture = new Texture("back.png");
        logoTexture = new Texture("wordduel.png");
        volumeTexture = new Texture("volume.png");
        muteTexture = new Texture("mute.png");

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

        // Font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("cinzel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 54;
        param.color = Color.GRAY;
        font = gen.generateFont(param);

        FreeTypeFontGenerator.FreeTypeFontParameter smallParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParam.size = 30; // Smaller size
        smallParam.color = Color.DARK_GRAY;
        smallFont = gen.generateFont(smallParam);

        gen.dispose();

        // Input fields
        nicknameField = new TextField("", createTextFieldStyle());
        nicknameField.setMessageText("Nickname");
        nicknameField.setSize(720, 120);
        nicknameField.setPosition(180, 1720);
        stage.addActor(nicknameField);
        nicknameBox = new Rectangle(170, 1725, 740, 120);

        errorLabel = new Label("", new Label.LabelStyle(font, Color.RED));
        errorLabel.setFontScale(1f);
        errorLabel.setPosition(180, 1650); // adjust Y-position if overlapping
        stage.addActor(errorLabel);


        // Buttons
        roundRects = new Rectangle[3];
        difficultyRects = new Rectangle[2];
        for (int i = 0; i < 3; i++) {
            roundRects[i] = new Rectangle(130 + i * 270, 1350, 240, 130);
        }
        for (int i = 0; i < 2; i++) {
            difficultyRects[i] = new Rectangle(210 + i * 330, 1050, 300, 130);
        }

        createButtonRect = new Rectangle(324, 450, 432, 144);
        backButtonRect = new Rectangle(324, 200, 432, 144);

        setupVolumeButton();
    }

    private void setupVolumeButton() {
        TextureRegionDrawable iconDrawable = new TextureRegionDrawable(
                new TextureRegion(game.isMuted() ? muteTexture : volumeTexture));
        volumeButton = new ImageButton(iconDrawable);
        volumeButton.setSize(100, 100);
        volumeButton.setPosition(stage.getViewport().getWorldWidth() - 110,
                stage.getViewport().getWorldHeight() - 110);
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

        font.draw(batch, "ENTER NICKNAME", 150, 1920);
        batch.draw(whiteBox, nicknameBox.x, nicknameBox.y, nicknameBox.width, nicknameBox.height);

        font.draw(batch, "SELECT ROUNDS", 150, 1540);
        drawButton(batch, roundRects[0], selectedRounds.equals("3") ? threeYellow : threeBronze);
        drawButton(batch, roundRects[1], selectedRounds.equals("5") ? fiveYellow : fiveBronze);
        drawButton(batch, roundRects[2], selectedRounds.equals("7") ? sevenYellow : sevenBronze);
        smallFont.draw(batch, "(Only 5-round games are tracked in the leaderboard)", 100, 1325);


        font.draw(batch, "SELECT DIFFICULTY", 150, 1240);
        drawButton(batch, difficultyRects[0], selectedDifficulty.equals("normal") ? normalYellow : normalBronze);
        drawButton(batch, difficultyRects[1], selectedDifficulty.equals("hard") ? hardYellow : hardBronze);

        batch.draw(createButtonTexture, createButtonRect.x, createButtonRect.y, createButtonRect.width, createButtonRect.height);
        batch.draw(backButtonTexture, backButtonRect.x, backButtonRect.y, backButtonRect.width, backButtonRect.height);
        batch.end();

        handleInput();
        stage.act(delta);
        stage.draw();
    }

    private void drawButton(SpriteBatch batch, Rectangle rect, Texture texture) {
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) return;

        Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        stage.getViewport().unproject(touch);

        for (int i = 0; i < roundRects.length; i++) {
            if (roundRects[i].contains(touch)) selectedRounds = roundOptions[i];
        }

        for (int i = 0; i < difficultyRects.length; i++) {
            if (difficultyRects[i].contains(touch)) selectedDifficulty = difficultyOptions[i];
        }

        if (createButtonRect.contains(touch)) {
            String nickname = nicknameField.getText().trim();
            if (nickname.isEmpty()) {
                errorLabel.setText("Please enter a nickname");
                return;
            } else {
                errorLabel.setText(""); // Clear previous error if input is valid
                controller.createLobby(
                        nickname,
                        Integer.parseInt(selectedRounds),
                        selectedDifficulty.toUpperCase(),
                        new LobbyServiceInterface.CreateCallback() {
                            @Override
                            public void onSuccess(String pin) {
                                Gdx.app.postRunnable(() -> {
                                    session.setTotalRounds(Integer.parseInt(selectedRounds));
                                    session.getLobby().setPin(pin);
                                    Player host = new Player(nickname, nickname);
                                    session.setLocalPlayer(host);
                                    session.getLobby().updatePlayersFromMap(Map.of(host.getId(), nickname));
                                    game.setScreen(new LobbyView(game, session, controller));
                                });
                            }

                            @Override
                            public void onError(String msg) {
                                Gdx.app.postRunnable(() -> showAlert(msg));
                            }
                        }
                );
            }

        }

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

        float x = (stage.getViewport().getWorldWidth() - dialog.getWidth()) / 2f;
        float y = (stage.getViewport().getWorldHeight() - dialog.getHeight()) / 2f - 300f;
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
        smallFont.dispose();
        backgroundTexture.dispose();
        createButtonTexture.dispose();
        backButtonTexture.dispose();
        logoTexture.dispose();
        volumeTexture.dispose();
        muteTexture.dispose();
        whiteBox.dispose();
        threeBronze.dispose(); fiveBronze.dispose(); sevenBronze.dispose();
        threeYellow.dispose(); fiveYellow.dispose(); sevenYellow.dispose();
        normalBronze.dispose(); hardBronze.dispose();
        normalYellow.dispose(); hardYellow.dispose();
    }
}
