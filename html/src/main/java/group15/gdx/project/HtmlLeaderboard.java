package group15.gdx.project;

import com.badlogic.gdx.Gdx;

/** Html implementation, same as Lwjgl3Leaderboard **/
public class HtmlLeaderboard implements Leaderboard {
    public void submitScore(String user, int score) {
        Gdx.app.log("HtmlLeaderboard", "would have submitted score for user " + user + ": " + score);
    }
}
