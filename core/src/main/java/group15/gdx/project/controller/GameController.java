package group15.gdx.project.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.Player;
import group15.gdx.project.view.Leaderboard;

/**
 * Game controller logic
 */

public class GameController {
    private final GameSession gameSession;
    private final Random random = new Random();

    // Our in-memory dictionary: sortedKey -> all valid words
    private final Map<String, List<String>> dictionaryMap = new HashMap<>();
    // For quick random selection of a key
    private List<String> dictionaryKeys = new ArrayList<>();

    public GameController(GameSession session) {
        this.gameSession = session;
        loadDictionary("ExpandedGroupedDictionary_3_7_10k_v2.txt");
    }

    private void loadDictionary(String fileName) {
        // Use LibGDX FileHandle to ensure the file is accessible on Android.
        FileHandle file = Gdx.files.internal(fileName);
        String content = file.readString();

        // Splitting file content into lines.
        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Expect line to be in the format "sortedKey -> word1, word2, word3, ...".
            String[] parts = line.split("->");
            if (parts.length != 2) continue;

            String sortedKey = parts[0].trim();
            String wordListStr = parts[1].trim();
            String[] rawWords = wordListStr.split(",");
            List<String> words = new ArrayList<>();
            for (String w : rawWords) {
                String trimmed = w.trim();
                if (!trimmed.isEmpty()) {
                    // Convert to lowercase - needed for consistency.
                    words.add(trimmed.toLowerCase());
                }
            }
            dictionaryMap.put(sortedKey, words);
        }
        dictionaryKeys = new ArrayList<>(dictionaryMap.keySet());

        // Debug log: check dictionary size
        System.out.println("Loaded dictionary keys count: " + dictionaryKeys.size());
    }

    public void generateLetters() {
        String sortedKey;
        do {
            int idx = random.nextInt(dictionaryKeys.size());
            sortedKey = dictionaryKeys.get(idx);
        } while (containsDuplicateLetters(sortedKey));
        char[] letters = sortedKey.toCharArray();
        for (int i = 0; i < letters.length; i++) {
            int swapIdx = random.nextInt(letters.length);
            char temp = letters[i];
            letters[i] = letters[swapIdx];
            letters[swapIdx] = temp;
        }
        String scrambled = new String(letters);

        gameSession.setCurrentLetters(scrambled);
        gameSession.setActiveSortedKey(sortedKey);
        gameSession.getGuessedWords().clear();

        // For debugging: display possible correct words in the console on application lauch (not android):
        displayPossibleWords();
    }

    private boolean containsDuplicateLetters(String str){
        // skips if empty
        if (str == null || str.isEmpty()) {
            return false;
        }

        boolean[] seen = new boolean[26]; // one bool for each letter in alphabet

        for (char letter : str.toCharArray()) {
            int index = letter - 'a'; // letter - 97:  to find position in alphabet
            if (index >= 0 && index < 26) { // Make sure it's a lowercase letter
                if (seen[index]) {
                    System.out.println("Duplicate letters! retrying");
                    return true; // Duplicate found
                }
                seen[index] = true;
            }
        }

        return false;
    }

    public boolean submitWord(String playerName, String word) {
        String lowerWord = word.toLowerCase();

        // Ensure the guess can be formed from the available letters.
        if (!canFormWord(gameSession.getCurrentLetters(), lowerWord)) {
            return false;
        }

        // Retrieve the active key and the list of valid words for that key.
        String activeKey = gameSession.getActiveSortedKey();
        List<String> validWords = dictionaryMap.get(activeKey);

        // Check if the word is among the valid answers.
        if (validWords != null && validWords.contains(lowerWord)) {
            // Check if the word hasn't been guessed already.
            if (!gameSession.getGuessedWords().contains(lowerWord)) {
                gameSession.getGuessedWords().add(lowerWord);
                Player p = findPlayer(playerName);
                if (p != null) {
                    p.addScore(lowerWord.length());
                }

                // Debug: print all guessed words vs. valid words
                System.out.println("Guessed words: " + gameSession.getGuessedWords());
                System.out.println("All possible words: " + validWords);

                // If the user has found all possible words, notify them.
                if (gameSession.getGuessedWords().size() == validWords.size()) {
                    System.out.println("Congratulations! You've found all possible words!");
                }
                return true;
            }
        }
        return false;
    }

    private Player findPlayer(String playerName) {
        for (Player p : gameSession.getLobby().getPlayers()) {
            if (p.getName().equalsIgnoreCase(playerName)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Prints the list of all valid words for the current active puzzle key to the console.
     */
    public void displayPossibleWords() {
        String activeKey = gameSession.getActiveSortedKey();
        List<String> possibleWords = dictionaryMap.get(activeKey);
        if (possibleWords != null && !possibleWords.isEmpty()) {
            System.out.println("Possible correct words for key '" + activeKey + "':");
            for (String w : possibleWords) {
                System.out.println(w);
            }
        } else {
            System.out.println("No possible words found for key: " + activeKey);
        }
    }

    /**
     * Checks if the word can be formed using the given available letters.
     * Each letter in the guess must be found in the availableLetters string.
     * When a letter is used, it is removed from a temporary copy so it isn’t reused.
     */
    private boolean canFormWord(String availableLetters, String word) {
        String temp = availableLetters.toLowerCase();
        for (char c : word.toLowerCase().toCharArray()) {
            int index = temp.indexOf(c);
            if (index == -1) {
                return false;
            }
            // Remove the used letter from temp
            temp = temp.substring(0, index) + temp.substring(index + 1);
        }
        return true;
    }
}
