package group15.gdx.project.midi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Class for game song.
 * The player should be able to mute the gamesong.
 */
public class GameSong {
    private Music music;
    private boolean isPlaying;
    private float volume = 1.0f; // use later maybe?

    public GameSong() {
        music = Gdx.audio.newMusic(Gdx.files.internal("game_song.mp3"));
        music.setLooping(true);
        isPlaying = false;
    }

    public void play(){
        if (music != null && !isPlaying){
            music.play();
            isPlaying = true;
        }
    }


    public void stop(){
        if (music != null && isPlaying){
            music.pause();
            isPlaying = false;
        }
    }


    public boolean toggle(){
        if (isPlaying){
            stop();
        }
        else {
            play();
        }
        return isPlaying;
    }
}
