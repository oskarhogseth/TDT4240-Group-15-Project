package group15.gdx.project.controller;

public class ScoreController {
    public static ScoreController instance;
    public int score = 0;

    public ScoreController() {
        if (instance == null) {
            instance = this;
        } else {
            throw new RuntimeException("ScoreController singleton already exists");
        }
    }
}
