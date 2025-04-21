package group15.gdx.project.model;
import static com.badlogic.gdx.math.MathUtils.random;

// Kilde: https://stackoverflow.com/questions/33847225/generating-a-random-pin-of-5-digits

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameLobby {

    // remove final so we can set server‑generated PIN
    private String pin;
    private List<Player> players;

    public GameLobby() {
        this.players = new ArrayList<>();
        // you can still generate a default pin if you like,
        // but it will be overwritten by setPin(...) once the server returns it.
        this.pin = generateRandomPin();
    }

    /** Optional: create a lobby with a known PIN */
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

    @SuppressWarnings("DefaultLocale")
    private String generateRandomPin() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000); // 00000 - 99999
        return String.format("%05d", num); // Ensures 5 digits with leading zeroes
    }
}
