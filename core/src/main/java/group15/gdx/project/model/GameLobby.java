package group15.gdx.project.model;
import static com.badlogic.gdx.math.MathUtils.random;

// Kilde: https://stackoverflow.com/questions/33847225/generating-a-random-pin-of-5-digits

import java.security.SecureRandom;

public class GameLobby {
    private final String gamePin;
    public GameLobby() {
        this.gamePin = generateRandomPin();
    }

    @SuppressWarnings("DefaultLocale")
    private String generateRandomPin() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000);
        return String.format("%05d", num);
    }

    public String getGamePin() {
        return gamePin;
    }

}
