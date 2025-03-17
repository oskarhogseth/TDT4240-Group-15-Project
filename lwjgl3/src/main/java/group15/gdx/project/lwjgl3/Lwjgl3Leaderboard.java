package group15.gdx.project.lwjgl3;

import com.badlogic.gdx.Gdx;

import group15.gdx.project.Leaderboard;

/** Desktop implementation, we simply log invocations **/
public class Lwjgl3Leaderboard implements Leaderboard {
    public void submitScore(String user, int score) {
        Gdx.app.log("Lwjgl3Leaderboard", "would have submitted score for user " + user + ": " + score);
    }
}
