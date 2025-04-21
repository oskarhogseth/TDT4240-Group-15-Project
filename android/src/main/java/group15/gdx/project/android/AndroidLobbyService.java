package group15.gdx.project.android;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentReference;

import group15.gdx.project.controller.GameController;
import group15.gdx.project.controller.LobbyServiceInterface;
import group15.gdx.project.model.LetterSet;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class AndroidLobbyService implements LobbyServiceInterface {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration listener;
    private final SecureRandom rnd = new SecureRandom();

    // Generate a 5‑digit PIN, retrying on collision
    private Task<String> generateUniquePin() {
        String pin = String.format("%05d", rnd.nextInt(100_000));
        DocumentReference ref = db.collection("lobbies").document(pin);
        return ref.get()
            .continueWithTask(t -> {
                if (t.getResult().exists()) {
                    // collision—try again
                    return generateUniquePin();
                } else {
                    return Tasks.forResult(pin);
                }
            });
    }

    @Override
    public void startGame(String pin, LetterSet letters) {
        Map<String,Object> update = new HashMap<>();
        update.put("isStarted",  true);
        update.put("letters",    letters.getScrambled());
        update.put("sortedKey",  letters.getSortedKey());
        db.collection("lobbies").document(pin).update(update);
    }

    @Override
    public void createLobby(
        String nickname,
        int rounds,
        String difficulty,
        CreateCallback callback
    ) {
        generateUniquePin()
            .addOnSuccessListener(pin -> {
                Map<String, Object> data = new HashMap<>();
                data.put("hostId", nickname);
                data.put("rounds", rounds);
                data.put("difficulty", difficulty);
                data.put("isStarted", false);

                Map<String, Object> players = new HashMap<>();
                players.put(nickname, Map.of("nickname", nickname));
                data.put("players", players);

                db.collection("lobbies")
                    .document(pin)
                    .set(data)
                    .addOnSuccessListener(v -> callback.onSuccess(pin))
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void joinLobby(
        String pin,
        String nickname,
        JoinCallback callback
    ) {
        DocumentReference ref = db.collection("lobbies").document(pin);
        ref.get()
            .addOnSuccessListener(snapshot -> {
                if (!snapshot.exists()) {
                    callback.onError("Invalid PIN");
                    return;
                }
                ref.update("players." + nickname, Map.of("nickname", nickname))
                    .addOnSuccessListener(u -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override @SuppressWarnings("unchecked")
    public void listenToLobby(String pin, PlayerUpdateCallback cb) {
        listener = db.collection("lobbies").document(pin)
            .addSnapshotListener((snap, err) -> {
                if (err != null || snap == null || !snap.exists()) return;

                // 1) players map
                Map<String,Object> raw = (Map<String,Object>) snap.get("players");
                Map<String,String> players = new HashMap<>();
                for (Map.Entry<String,Object> e : raw.entrySet()) {
                    Map<String,Object> info = (Map<String,Object>) e.getValue();
                    players.put(e.getKey(), (String) info.get("nickname"));
                }
                cb.onPlayersUpdated(players);

                // 2) game‑started signal
                if (Boolean.TRUE.equals(snap.getBoolean("isStarted"))) {
                    String scrambled = snap.getString("letters");
                    String key       = snap.getString("sortedKey");
                    cb.onGameStarted(new LetterSet(scrambled, key));
                }
            });
    }

    @Override
    public void startGame(String pin) {
        db.collection("lobbies")
            .document(pin)
            .update("isStarted", true);
    }
}
