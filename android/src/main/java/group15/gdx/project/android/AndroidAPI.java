package group15.gdx.project.android;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

import group15.gdx.project.API;
import group15.gdx.project.model.Player;
import group15.gdx.project.model.Score;

/** Android implementation, can access PlayGames directly **/
public class AndroidAPI implements API {
    FirebaseDatabase database;
    DatabaseReference highscores;

    public AndroidAPI() {
        database = FirebaseDatabase.getInstance();
        highscores = database.getReference("highscores");
    }

    @Override
    public void submitScore(Score score) {
        highscores.push().setValue(score);
    }

    @Override
    public void getHighscores(ArrayList<Score> dataHolder) {
        System.out.println("Getting highscores");
        highscores.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                System.out.println("Got highscores");
                Iterable<DataSnapshot> response = task.getResult().getChildren();
                for (DataSnapshot child : response) {
                    String player = child.child("player").getValue(String.class);
                    int score = child.child("score").getValue(Integer.class);
                    dataHolder.add(new Score(player, score));
                }
                Collections.sort(dataHolder);
            }
        });
    }
}
