package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

public class LogInView extends ScreenAdapter {

    private static final String TITLE = "Welcome to Word Duel";
    private static final String ENTER_NAME = "Please enter your name:";
    private static final String LOGIN_BUTTON = "Join Lobby";
    private static final String YOUR_NAME = "Your name";
    private final Launcher game;
    private final GameSession gameSession;

    private Stage stage;
    private Skin skin;
    private TextField nameField;

    public LogInView(Launcher game, GameSession session) {
        this.game = game;
        this.gameSession = session;

        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("vhs.json"));

        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        float baseFont = screenHeight / 40f;

        // Title
        Label titleLabel = new Label(TITLE, skin);
        titleLabel.setFontScale(baseFont / 20f);
        table.add(titleLabel).colspan(2).center().padBottom(screenHeight * 0.1f);
        table.row();

        // Name field instructions
        Label nameLabel = new Label(ENTER_NAME, skin);
        nameLabel.setFontScale(baseFont / 22f);
        table.add(nameLabel).colspan(2).center().padBottom(screenHeight * 0.03f);
        table.row();

        // Name input field
        nameField = new TextField("", skin);
        nameField.setMessageText(YOUR_NAME);
        table.add(nameField)
            .width(screenWidth * 0.7f)
            .height(screenHeight * 0.07f)
            .colspan(2)
            .center()
            .padBottom(screenHeight * 0.05f);
        table.row();

        // Join lobby button
        TextButton loginButton = new TextButton(LOGIN_BUTTON, skin);
        loginButton.getLabel().setFontScale(baseFont / 22f);
        loginButton.setColor(0.2f, 0.6f, 0.8f, 1);
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String playerName = nameField.getText().trim();
                if (!playerName.isEmpty()) {
                    login(playerName);
                }
            }
        });

        table.add(loginButton)
            .width(screenWidth * 0.5f)
            .height(screenHeight * 0.08f)
            .colspan(2)
            .center();
    }

    private void login(String playerName) {
        Player player = new Player(playerName);
        gameSession.getLobby().addPlayer(player);

        //ADD ANOTHER PLAYER TO THE LOBBY UNTIL WE HAVE IMPLEMENTED MULTIPLAYER (REMOVE THIS BEFORE SUBMITTING)
        Player player2 = new Player("Bob");
        gameSession.getLobby().addPlayer(player2);

        game.setScreen(new LobbyView(game, gameSession));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
