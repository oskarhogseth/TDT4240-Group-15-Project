package group15.gdx.project.android;

import group15.gdx.project.Leaderboard;

/** Android implementation, can access PlayGames directly **/
public class AndroidLeaderboard implements Leaderboard {

    public void submitScore(String user, int score) {
        // Ignore the user name, because Google Play reports the score for the currently signed-in player
        // See https://developers.google.com/games/services/android/signin for more information on this

        // lurer på om dette er noe vi skal implementere etterhvert som vi har en playgame klasse/funksjon
        //PlayGames.getLeaderboardsClient(activity).submitScore(getString(R.string.leaderboard_id), score);
    }
}
