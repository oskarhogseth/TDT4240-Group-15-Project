package group15.gdx.project.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Game controller logic
 */
public class GameController {
    private GameSession gameSession;
    private Random random = new Random();

    FileHandle file = Gdx.files.internal("FrequentWords.txt");
    String dictionaryData = file.readString();
    List<String> dictionary = Arrays.asList(dictionaryData.split("\\r?\\n"));

    public GameController(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    /**
     * Generate random letters (e.g., 7 letters)
     */
    public void generateLetters() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String candidate;
        int wordCount;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                int index = random.nextInt(alphabet.length());
                sb.append(alphabet.charAt(index));
            }
            candidate = sb.toString();
            wordCount = countFormableWords(candidate, dictionary);

        } while (wordCount > 2 && wordCount < 5);

        List<Character> letters = new ArrayList<>();
        for (char c : candidate.toCharArray()) {
            letters.add(c);
        }

        gameSession.setCurrentLetters(letters);
    }

    public String getCurrentLetters() {
        StringBuilder sb = new StringBuilder();
        for (Character c : gameSession.getCurrentLetters()) {
            sb.append(c);
        }
        return sb.toString();
    }
    /**
     * Count how many words in the dictionary can be formed from the given letter set.
     *
     * @param letters Candidate letter set.
     * @param dictionary List of valid words.
     * @return The count of formable words.
     */
    private int countFormableWords(String letters, List<String> dictionary) {
        int count = 0;
        for (String word : dictionary) {
            if (canFormWord(letters, word)) {
                count++;
            }
        }
        return count;
    }

    public List<String> getFormableWords(String letters) {
        List<String> validWords = new ArrayList<>();
        for (String word : dictionary) {
            if (canFormWord(letters, word)) {
                validWords.add(word);
            }
        }
        return validWords;
    }

    public boolean submitWord(Player player, String word) {
        if (!canFormWord(getCurrentLetters(), word)) {
            return false;
        }

        if (!dictionary.contains(word.toLowerCase())) {
            return false;
        }

        if (player != null) {
            player.addScore(word.length());
            return true;
        }
        return false;
    }
    /**
     * Check if a word can be formed from the given set of letters.
     *
     * @param letters The available letters.
     * @param word The word to form.
     * @return True if the word can be formed; false otherwise.
     */
    private boolean canFormWord(String letters, String word) {
        String tempLetters = letters.toLowerCase();
        for (char c : word.toLowerCase().toCharArray()) {
            int index = tempLetters.indexOf(c);
            if (index == -1) {
                return false;
            }
            tempLetters = tempLetters.substring(0, index) + tempLetters.substring(index + 1);
        }
        return true;
    }

    private Player findPlayer(String name) {
        for (Player p : gameSession.getLobby().getPlayers()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }
}
