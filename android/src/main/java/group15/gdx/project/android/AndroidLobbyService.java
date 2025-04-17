package group15.gdx.project.android;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentReference;
import group15.gdx.project.controller.LobbyServiceInterface;

import java.util.HashMap;
import java.util.Map;

public class AndroidLobbyService implements LobbyServiceInterface {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration listener;

    @Override
    public void createLobby(String pin, String nickname) {
        Map<String, Object> data = new HashMap<>();
        data.put("hostId", nickname);
        data.put("isStarted", false);
        data.put("players", new HashMap<String, Object>() {{
            put(nickname, new HashMap<String, Object>() {{
                put("nickname", nickname);
            }});
        }});
        db.collection("lobbies").document(pin).set(data);
    }

    @Override
    public void joinLobby(String pin, String nickname, Runnable onSuccess, Runnable onFail) {
        DocumentReference ref = db.collection("lobbies").document(pin);
        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                ref.update("players." + nickname, new HashMap<String, Object>() {{
                        put("nickname", nickname);
                    }}).addOnSuccessListener(unused -> onSuccess.run())
                    .addOnFailureListener(e -> onFail.run());
            } else {
                onFail.run();
            }
        });
    }

    @Override
    public void listenToLobby(String pin, PlayerUpdateCallback callback) {
        listener = db.collection("lobbies").document(pin)
            .addSnapshotListener((snapshot, error) -> {
                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> raw = (Map<String, Object>) snapshot.get("players");
                    Map<String, String> players = new HashMap<>();
                    for (String key : raw.keySet()) {
                        Map<String, Object> value = (Map<String, Object>) raw.get(key);
                        players.put(key, (String) value.get("nickname"));
                    }
                    callback.onPlayersUpdated(players);
                    if (Boolean.TRUE.equals(snapshot.getBoolean("isStarted"))) {
                        callback.onGameStarted();
                    }
                }
            });
    }

    @Override
    public void startGame(String pin) {
        db.collection("lobbies").document(pin).update("isStarted", true);
    }
}
