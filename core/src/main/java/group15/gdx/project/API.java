package group15.gdx.project;

import java.util.ArrayList;

import group15.gdx.project.model.Score;

public interface API {
    void submitScore(Score score);
    void getHighscores(ArrayList<Score> dataHolder);
}
