package group15.gdx.project.android;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import group15.gdx.project.Launcher;
import group15.gdx.project.controller.LobbyServiceInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;


public class AndroidLauncher extends AndroidApplication {
    private static final String TAG = "AndroidLauncher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1) Set up LibGDX immediately so onResume() won't NPE
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;

        Launcher launcher = new Launcher(new AndroidAPI());
        LobbyServiceInterface service = new AndroidLobbyService();
        launcher.setLobbyService(service);

        initialize(launcher, config);  // ← must do this before returning from onCreate()

        // 2) Then sign in to Firebase in the background
        FirebaseAuth.getInstance()
            .signInAnonymously()
            .addOnSuccessListener((AuthResult auth) -> {
                Log.d(TAG, "Anonymous sign‑in OK: " + auth.getUser().getUid());
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Anonymous sign‑in failed", e);
            });
    }
}
