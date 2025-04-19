package group15.gdx.project;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

import group15.gdx.project.model.Score;

/** Html implementation, same as Lwjgl3Leaderboard **/
public class HtmlAPI implements API {

    @Override
    public void submitScore(Score score) {
        Gdx.app.log("HtmlLeaderboard", "would have submitted score for user " + score.player + ": " + score.score);
    }

    @Override
    public void getHighscores(ArrayList<Score> dataHolder) {

    }
}
