package group15.gdx.project.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import group15.gdx.project.API;
import group15.gdx.project.Launcher;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;
import group15.gdx.project.model.Score;

import java.util.ArrayList;

public class Leaderboard extends ScreenAdapter {
    private final Launcher game;
    private final Stage stage;
    private final Skin skin;
    private final Table mainTable;
    private final Table leaderboardTable;
    private final GameSession gameSession;

    private ArrayList<Score> leaderboard = new ArrayList<>();
    private API api;

    public Leaderboard(Launcher game, GameSession session) {
        this.game = game;
        this.gameSession = session;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("vhs.json"));
        this.mainTable = new Table();
        this.leaderboardTable = new Table();

        this.api = game.getAPI();
        // for each player submit their score
        for (Player player : gameSession.getLobby().getPlayers()) {
            submitHighscore(player, player.getScore());
        }

        fetchLeaderboard();

        setupUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void fetchLeaderboard() {
        //this.leaderboard.clear();
        api.getHighscores(this.leaderboard, () -> {
            System.out.println("Highscores loaded");
            populateLeaderboardTable();
        });
    }

    public void submitHighscore(Player player, int score) {
        api.submitScore(new Score(player.getName(), score));
    }

    private void setupUI() {
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Title
        Label titleLabel = new Label("Scoreboard", skin);
        titleLabel.setFontScale(2f);
        mainTable.add(titleLabel).padBottom(20).center();
        mainTable.row();

        // Leaderboard table
        leaderboardTable.top().padTop(10);

        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        mainTable.add(scrollPane).expand().fill();
        mainTable.row();

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new LogInView(game, gameSession, game.getLobbyController()));
            }
        });
        mainTable.add(backButton).padTop(20).width(200).height(50).center();
    }

    private void populateLeaderboardTable() {
        // Clear previous entries
        leaderboardTable.clear();

        // Add headers
        leaderboardTable.add(new Label("Rank", skin)).width(100).align(Align.center);
        leaderboardTable.add(new Label("Player", skin)).width(200).align(Align.center);
        leaderboardTable.add(new Label("Score", skin)).width(100).align(Align.center);
        leaderboardTable.row();

        // Add scores
        int displayCount = Math.min(leaderboard.size(), 10);

        System.out.println("Leaderboard size: " + leaderboard.size());
        for (int i = 0; i < displayCount; i++) {
            Score score = leaderboard.get(i);
            System.out.println("Score: " + score.getPlayer() + " - " + score.getScore());
            leaderboardTable.add(new Label(String.valueOf(i + 1), skin)).align(Align.center);
            leaderboardTable.add(new Label(score.getPlayer(), skin)).align(Align.left);
            leaderboardTable.add(new Label(String.valueOf(score.getScore()), skin)).align(Align.center);
            leaderboardTable.row();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
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
