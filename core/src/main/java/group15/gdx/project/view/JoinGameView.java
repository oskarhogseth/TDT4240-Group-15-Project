package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
    private Skin skin;
    private BitmapFont cinzelFont;
    private Texture backgroundTexture;

    private TextField nameField;
    private TextField pinField;

    public JoinGameView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(1080, 2400)); // 🔁 Adjusted for tall screen
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        skin = new Skin(Gdx.files.internal("vhs.json"));

        cinzelFont = loadCinzelFont(64);
        skin.get(Label.LabelStyle.class).font = cinzelFont;
        skin.get(TextButton.TextButtonStyle.class).font = cinzelFont;
        skin.get(TextField.TextFieldStyle.class).font = cinzelFont;

        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(screenHeight * 0.08f);
        stage.addActor(table);

        Label title = new Label("Join a Game", skin);
        title.setColor(Color.BLACK);
        title.setFontScale(1.6f);
        table.add(title).colspan(2).padBottom(screenHeight * 0.04f);
        table.row();

        nameField = new TextField("", skin);
        nameField.setMessageText("Nickname");
        table.add(nameField)
                .width(screenWidth * 0.75f)
                .height(screenHeight * 0.07f)
                .colspan(2)
                .padBottom(screenHeight * 0.04f);
        table.row();

        pinField = new TextField("", skin);
        pinField.setMessageText("Game PIN");
        table.add(pinField)
                .width(screenWidth * 0.75f)
                .height(screenHeight * 0.07f)
                .colspan(2)
                .padBottom(screenHeight * 0.06f);
        table.row();

        TextButton joinBtn = new TextButton("Join Game", skin);
        joinBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String nickname = nameField.getText().trim();
                String pin = pinField.getText().trim();
                if (!nickname.isEmpty() && !pin.isEmpty()) {
                    joinGame(nickname, pin);
                }
            }
        });

        table.add(joinBtn)
                .width(screenWidth * 0.5f)
                .height(screenHeight * 0.08f)
                .colspan(2)
                .center();
    }

    private void joinGame(String nickname, String pin) {
        // TODO: Firebase PIN check in future
        Player player = new Player("id-" + nickname, nickname);
        session.setLocalPlayer(player);
        session.getLobby().addPlayer(player);

        // TEMP: Add host player
        session.getLobby().addPlayer(new Player("id-host", "Host"));

        game.setScreen(new LobbyView(game, session, controller));
    }

    private BitmapFont loadCinzelFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("cinzel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = Color.BLACK;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        skin.dispose();
        cinzelFont.dispose();
    }
}
