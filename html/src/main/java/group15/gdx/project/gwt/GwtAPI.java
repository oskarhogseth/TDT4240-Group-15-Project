package group15.gdx.project.gwt;

import java.util.ArrayList;

import group15.gdx.project.API;
import group15.gdx.project.model.Score;

public class GwtAPI implements API {

    @Override
    public void submitScore(Score score) {
        // GWT does not support Firebase, so we cannot submit scores
        // This is a placeholder implementation
        System.out.println("GwtAPI: would have submitted score for user " + score.getPlayer() + ": " + score.getScore());
    }

    @Override
    public void getHighscores(ArrayList<Score> dataHolder, HighscoresCallback callback) {
        // GWT does not support Firebase, so we cannot fetch scores
        // This is a placeholder implementation
        System.out.println("GwtAPI: would have fetched highscores");
        dataHolder.add(new Score("Player1", 100));
        dataHolder.add(new Score("Player2", 200));
        dataHolder.add(new Score("Player3", 300));
        dataHolder.add(new Score("Player4", 400));
        dataHolder.add(new Score("Player5", 500));
    }
}
