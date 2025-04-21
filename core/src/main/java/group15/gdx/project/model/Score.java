package group15.gdx.project.model;

public class Score implements Comparable<Score> {
    public int score;
    public String player;

    public Score() {
        score = 0;
        player = null;
    }
    public Score(String player, int score) {
        this.score = score;
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public String getPlayer() {
        return player;
    }

    public String toString() {
        return player + ": " + score;
    }

    @Override
    public int compareTo(Score o) {
        if (score > o.score) {
            return -1;
        }
        else if (score < o.score) {
            return 1;
        }
        else {
            return player.compareTo(o.player);
        }
    }
}
