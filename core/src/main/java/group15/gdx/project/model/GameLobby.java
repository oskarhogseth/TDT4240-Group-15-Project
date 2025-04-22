package group15.gdx.project.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameLobby {

    private String pin;
    private List<Player> players;

    private LetterSet currentLetterSet;

    public GameLobby() {
        this.players = new ArrayList<>();
        this.pin = generateRandomPin();
    }

    public GameLobby(String pin) {
        this.pin = pin;
        this.players = new ArrayList<>();
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void updatePlayersFromMap(Map<String, String> playerMap) {
        players.clear();
        for (Map.Entry<String, String> entry : playerMap.entrySet()) {
            players.add(new Player(entry.getKey(), entry.getValue()));
        }
    }

    // Shared letter set access
    public LetterSet getCurrentLetterSet() {
        return currentLetterSet;
    }

    public void setCurrentLetterSet(LetterSet set) {
        this.currentLetterSet = set;
    }

    @SuppressWarnings("DefaultLocale")
    private String generateRandomPin() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000); // 00000 - 99999
        return String.format("%05d", num);
    }
}
