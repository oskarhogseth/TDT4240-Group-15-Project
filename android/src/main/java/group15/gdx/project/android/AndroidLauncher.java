package group15.gdx.project.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import group15.gdx.project.Launcher;
import group15.gdx.project.android.AndroidLobbyService;
import group15.gdx.project.controller.LobbyServiceInterface;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;


        Launcher launcher = new Launcher();
        LobbyServiceInterface service = new AndroidLobbyService();
        launcher.setLobbyService(service);

        initialize(launcher, configuration);
    }
}
