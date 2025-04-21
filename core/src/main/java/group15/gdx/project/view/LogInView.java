package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyController;
import group15.gdx.project.model.GameSession;

public class LogInView extends ScreenAdapter {

    private final Launcher game;
    private final GameSession session;
    private final LobbyController controller;

    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private BitmapFont cinzelFont;

    private Texture backgroundTexture;
    private Texture logoTexture;
    private Texture createGameTexture;
    private Texture joinGameTexture;

    public LogInView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        // Load assets
        backgroundTexture = new Texture("background.png");
        logoTexture = new Texture("wordduel.png");
        createGameTexture = new Texture("createnewgame.png");
        joinGameTexture = new Texture("joingame.png");

        // Load skin and Cinzel font
        skin = new Skin(Gdx.files.internal("vhs.json"));
        cinzelFont = loadCinzelFont(30);
        skin.get(Label.LabelStyle.class).font = cinzelFont;

        setupUI();
    }

    private void setupUI() {
        float screenHeight = stage.getViewport().getWorldHeight();
        float screenWidth = stage.getViewport().getWorldWidth();

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);


        Image logoImage = new Image(logoTexture);
        logoImage.setSize(screenWidth * 0.8f, screenHeight * 0.25f);
        table.add(logoImage).colspan(2).padBottom(screenHeight * 0.07f).center();
        table.row();


        ImageButton createButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(createGameTexture)));
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CreateGameView(game, session, controller));
            }
        });


        ImageButton joinButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(joinGameTexture)));
        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new JoinGameView(game, session, controller));
            }
        });


        Table buttonRow = new Table();
        buttonRow.add(createButton).width(180).height(70).padRight(20);
        buttonRow.add(joinButton).width(180).height(70).padLeft(20);

        table.add(buttonRow).colspan(2).padTop(screenHeight * 0.03f).expandY().bottom();
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
        logoTexture.dispose();
        createGameTexture.dispose();
        joinGameTexture.dispose();
        cinzelFont.dispose();
        skin.dispose();
    }
}
