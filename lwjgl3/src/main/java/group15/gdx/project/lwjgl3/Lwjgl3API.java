package group15.gdx.project.lwjgl3;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

import group15.gdx.project.API;
import group15.gdx.project.model.Score;

/** Desktop implementation, we simply log invocations **/
public class Lwjgl3API implements API {

    @Override
    public void submitScore(Score score) {
        Gdx.app.log("Lwjgl3Leaderboard", "would have submitted score for user " + score.getPlayer() + ": " + score.getScore());
    }

    @Override
    public void getHighscores(ArrayList<Score> dataHolder, HighscoresCallback callback) {
        Gdx.app.log("Lwjgl3Leaderboard", "would have fetched highscores");
        dataHolder.add(new Score("Player1", 100));
        dataHolder.add(new Score("Player2", 200));
        dataHolder.add(new Score("Player3", 300));
        dataHolder.add(new Score("Player4", 400));
        dataHolder.add(new Score("Player5", 500));
        callback.onHighscoresLoaded();
    }
}
