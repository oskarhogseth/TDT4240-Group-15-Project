package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
    private Skin skin;
    private BitmapFont cinzelFont;
    private Texture backgroundTexture;
    private Texture createButtonTexture;

    private TextField nameField;
    private SelectBox<String> roundsSelect;
    private SelectBox<String> difficultySelect;

    public CreateGameView(Launcher game, GameSession session, LobbyController controller) {
        this.game = game;
        this.session = session;
        this.controller = controller;

        stage = new Stage(new FitViewport(1080, 2400));
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        backgroundTexture = new Texture("background.png");
        createButtonTexture = new Texture("creategame.png");
        skin = new Skin(Gdx.files.internal("vhs.json"));

        cinzelFont = loadCinzelFont(64);
        skin.get(Label.LabelStyle.class).font = cinzelFont;
        skin.get(TextButton.TextButtonStyle.class).font = cinzelFont;
        skin.get(TextField.TextFieldStyle.class).font = cinzelFont;

        // ✅ Register SelectBoxStyle before using
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.font = cinzelFont;
        selectBoxStyle.fontColor = Color.BLACK;
        selectBoxStyle.background = skin.newDrawable("textfield", Color.WHITE);
        selectBoxStyle.scrollStyle = new ScrollPane.ScrollPaneStyle();
        selectBoxStyle.listStyle = new List.ListStyle();
        selectBoxStyle.listStyle.font = cinzelFont;
        selectBoxStyle.listStyle.selection = skin.newDrawable("textfield", Color.LIGHT_GRAY);
        selectBoxStyle.listStyle.fontColorSelected = Color.BLACK;
        selectBoxStyle.listStyle.fontColorUnselected = Color.DARK_GRAY;
        skin.add("default", selectBoxStyle);

        setupUI();
    }

    private void setupUI() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(screenHeight * 0.05f);
        stage.addActor(table);

        Label prompt = new Label("Enter your nickname", skin);
        prompt.setColor(Color.BLACK);
        prompt.setFontScale(1.4f);
        table.add(prompt).padBottom(screenHeight * 0.03f).colspan(2);
        table.row();

        nameField = new TextField("", skin);
        nameField.setMessageText("Nickname");
        table.add(nameField)
                .width(screenWidth * 0.75f)
                .height(screenHeight * 0.07f)
                .colspan(2)
                .padBottom(screenHeight * 0.05f);
        table.row();

        // Rounds selector with placeholder
        Label roundsLabel = new Label("Rounds:", skin);
        roundsLabel.setColor(Color.BLACK);
        roundsLabel.setFontScale(1.2f);
        table.add(roundsLabel).padRight(20).right();

        roundsSelect = new SelectBox<>(skin);
        roundsSelect.setItems("--", "3", "5", "7");
        roundsSelect.setSelected("--");
        table.add(roundsSelect).width(160).left();
        table.row().padBottom(screenHeight * 0.06f);

        // Difficulty selector
        Label diffLabel = new Label("Difficulty:", skin);
        diffLabel.setColor(Color.BLACK);
        diffLabel.setFontScale(1.2f);
        table.add(diffLabel).padRight(20).right();

        difficultySelect = new SelectBox<>(skin);
        difficultySelect.setItems("None selected", "Easy", "Medium", "Hard");
        difficultySelect.setSelected("None selected");
        table.add(difficultySelect).width(200).left();
        table.row().padBottom(screenHeight * 0.06f);

        // Create game button
        ImageButton createButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(createButtonTexture)));
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String nickname = nameField.getText().trim();
                String roundStr = roundsSelect.getSelected();
                String difficulty = difficultySelect.getSelected();

                Integer rounds = "--".equals(roundStr) ? null : Integer.parseInt(roundStr);

                if (!nickname.isEmpty()) {
                    System.out.println("Nickname: " + nickname);
                    System.out.println("Rounds: " + (rounds != null ? rounds : "--"));
                    System.out.println("Difficulty: " + difficulty);

                    Player player = new Player("id-" + nickname, nickname);
                    session.setLocalPlayer(player);
                    session.getLobby().addPlayer(player);
                    game.setScreen(new LobbyView(game, session, controller));
                }
            }
        });

        table.add(createButton).size(300, 100).colspan(2).padTop(screenHeight * 0.03f);
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
        createButtonTexture.dispose();
        skin.dispose();
        cinzelFont.dispose();
    }
}
