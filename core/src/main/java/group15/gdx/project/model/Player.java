package group15.gdx.project.model;

import java.util.HashMap;
import java.util.Map;

public class Player {

    private String uid;
    private String nickname;
    private int score = 0;

    public Player(String uid, String nickname) {
        this.uid = uid;
        this.nickname = nickname;
    }

    // existing getters...
    public String getUid() {
        return uid;
    }
    public String getNickname() {
        return nickname;
    }
    public String getName() {
        return nickname;
    }
    public int getScore() {
        return score;
    }
    public void addScore(int amount) {
        this.score += amount;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("nickname", nickname);
        return map;
    }

    // alias for getUid() so CreateGameView#player.getId() compiles
    public String getId() {
        return uid;
    }
}
