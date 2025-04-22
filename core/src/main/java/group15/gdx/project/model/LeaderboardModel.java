package group15.gdx.project.model;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardModel {
    private final List<Score> scores = new ArrayList<>();

    public void setScores(List<Score> newScores) {
        scores.clear();
        scores.addAll(newScores);
    }

    public List<Score> getScores() {
        return scores;
    }
}
