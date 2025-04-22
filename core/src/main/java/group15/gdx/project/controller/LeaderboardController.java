package group15.gdx.project.controller;

import com.badlogic.gdx.Gdx;
import group15.gdx.project.API;
import group15.gdx.project.model.LeaderboardModel;
import group15.gdx.project.model.Score;

import java.util.List;

public class LeaderboardController {

    private final API api;
    private final LeaderboardModel model;

    public interface FetchCallback {
        void onComplete();
    }

    public LeaderboardController(API api, LeaderboardModel model) {
        this.api = api;
        this.model = model;
    }

    public void fetchScores(FetchCallback callback) {
        api.getHighscores(model.getScores(), () -> Gdx.app.postRunnable(callback::onComplete));
    }

    public List<Score> getScores() {
        return model.getScores();
    }
}
