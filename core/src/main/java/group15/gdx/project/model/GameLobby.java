package group15.gdx.project.model;
import static com.badlogic.gdx.math.MathUtils.random;

// Kilde: https://stackoverflow.com/questions/33847225/generating-a-random-pin-of-5-digits

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameLobby {

    private final String pin;
    private List<Player> players;

    public GameLobby() {
        this.pin = generateRandomPin();
        this.players = new ArrayList<>();
    }

    // Update player list from Firebase map
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

    @SuppressWarnings("DefaultLocale")
    private String generateRandomPin() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000); // 00000 - 99999
        return String.format("%05d", num); // Ensures 5 digits with leading zeroes
    }

    //public String getGamePin() {
    //    return gamePin;
    //}

}
