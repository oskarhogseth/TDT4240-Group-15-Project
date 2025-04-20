package group15.gdx.project.android;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        highscores.orderByChild("player").equalTo(score.getPlayer()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Player already exists in the database
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        int existingScore = child.child("score").getValue(Integer.class);
                        if (score.getScore() > existingScore) {
                            // Update the score if the new score is higher
                            child.getRef().child("score").setValue(score.getScore());
                            System.out.println("Score updated for player: " + score.getPlayer());
                        } else {
                            System.out.println("Existing score is higher or equal. No update needed.");
                        }
                    }
                } else {
                    // Player does not exist, add the new score
                    highscores.push().setValue(score);
                    System.out.println("New score added for player: " + score.getPlayer());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("Error checking player score: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void getHighscores(ArrayList<Score> dataHolder, HighscoresCallback callback) {
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
                System.out.println("Highscores: " + dataHolder);

                if (callback != null) {
                    callback.onHighscoresLoaded();
                }
            }
        });
    }
}
