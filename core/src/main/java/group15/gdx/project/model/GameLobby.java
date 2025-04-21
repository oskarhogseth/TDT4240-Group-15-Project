package group15.gdx.project.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameLobby {

    private String pin;
    private List<Player> players;

    public GameLobby() {
        this.pin = generateRandomPin();
        this.players = new ArrayList<>();
    }

    public GameLobby(String pin) {
        this.pin = pin;
        this.players = new ArrayList<>();
    }

    public void updatePlayersFromMap(Map<String, String> playerMap) {
        players.clear();
        for (Map.Entry<String, String> entry : playerMap.entrySet()) {
            players.add(new Player(entry.getKey(), entry.getValue()));
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    private String generateRandomPin() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000); // 00000 - 99999
        return String.format("%05d", num);
    }
}
