package group15.gdx.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import group15.gdx.project.controller.GameController;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

public class GameScreen extends ScreenAdapter {
    private SpriteBatch batch;
    private BitmapFont font;
    private GameSession gameStateData; // Data container (instead of enum-based GameState)
    private GameController gameController;

    public GameScreen() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Default font

        // Initialize your game data and controller
        gameStateData = new GameSession();
        gameController = new GameController(gameStateData);

        // Add sample players to the lobby
        gameStateData.getLobby().addPlayer(new Player("Alice"));
        gameStateData.getLobby().addPlayer(new Player("Bob"));

        // Generate the initial set of letters
        gameController.generateLetters();
    }

    @Override
    public void render(float delta) {
        // Clear the screen with a background color
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Display the current letters on-screen
        font.draw(batch, "Letters: " + gameStateData.getCurrentLetters(), 50, 400);

        // Display player scores for demonstration
        int y = 350;
        for (Player p : gameStateData.getLobby().getPlayers()) {
            font.draw(batch, p.getName() + " : " + p.getScore(), 50, y);
            y -= 30;
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
