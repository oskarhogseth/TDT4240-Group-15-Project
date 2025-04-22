package group15.gdx.project.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import group15.gdx.project.model.GameSession;
import group15.gdx.project.model.LetterSet;
import group15.gdx.project.model.Player;

import java.util.*;

public class GameController {
    private final GameSession gameSession;
    private final Random random = new Random();

    private final Map<String, List<String>> dictionaryMap = new HashMap<>();
    private List<String> dictionaryKeys = new ArrayList<>();

    public GameController(GameSession session) {
        this.gameSession = session;
        loadDictionary("ExpandedGroupedDictionary_3_7_10k_v2.txt");
    }

    private void loadDictionary(String fileName) {
        FileHandle file = Gdx.files.internal(fileName);
        String content = file.readString();

        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("->");
            if (parts.length != 2) continue;

            String sortedKey = parts[0].trim();
            String[] rawWords = parts[1].trim().split(",");
            List<String> words = new ArrayList<>();
            for (String w : rawWords) {
                String trimmed = w.trim().toLowerCase();
                if (!trimmed.isEmpty()) words.add(trimmed);
            }
            dictionaryMap.put(sortedKey, words);
        }
        dictionaryKeys = new ArrayList<>(dictionaryMap.keySet());
        System.out.println("Loaded dictionary keys count: " + dictionaryKeys.size());
    }

    public LetterSet generateLetters() {
        String sortedKey;
        do {
            int idx = random.nextInt(dictionaryKeys.size());
            sortedKey = dictionaryKeys.get(idx);
        } while (containsDuplicateLetters(sortedKey) || sortedKey.length() != 6);

        char[] letters = sortedKey.toCharArray();
        for (int i = 0; i < letters.length; i++) {
            int swap = random.nextInt(letters.length);
            char temp = letters[i];
            letters[i] = letters[swap];
            letters[swap] = temp;
        }
        String scrambled = new String(letters);

        gameSession.setCurrentLetters(scrambled);
        gameSession.setActiveSortedKey(sortedKey);
        gameSession.getGuessedWords().clear();

        return new LetterSet(scrambled, sortedKey);
    }

    public void loadLetters(LetterSet set) {
        gameSession.setCurrentLetters(set.getScrambled());
        gameSession.setActiveSortedKey(set.getSortedKey());
        gameSession.getGuessedWords().clear();
    }

    private boolean containsDuplicateLetters(String str) {
        if (str == null || str.isEmpty()) return false;

        boolean[] seen = new boolean[26];
        for (char letter : str.toCharArray()) {
            int index = letter - 'a';
            if (index >= 0 && index < 26) {
                if (seen[index]) return true;
                seen[index] = true;
            }
        }
        return false;
    }

    public boolean submitWord(String playerName, String word) {
        String lowerWord = word.toLowerCase();
        if (!canFormWord(gameSession.getCurrentLetters(), lowerWord)) return false;

        String activeKey = gameSession.getActiveSortedKey();
        List<String> validWords = dictionaryMap.get(activeKey);

        if (validWords != null && validWords.contains(lowerWord)) {
            if (!gameSession.getGuessedWords().contains(lowerWord)) {
                gameSession.getGuessedWords().add(lowerWord);
                Player p = findPlayer(playerName);
                if (p != null) {
                    p.addScore(lowerWord.length());
                }
                return true;
            }
        }
        return false;
    }

    private boolean canFormWord(String availableLetters, String word) {
        String temp = availableLetters.toLowerCase();
        for (char c : word.toCharArray()) {
            int index = temp.indexOf(c);
            if (index == -1) return false;
            temp = temp.substring(0, index) + temp.substring(index + 1);
        }
        return true;
    }

    private Player findPlayer(String playerName) {
        for (Player p : gameSession.getLobby().getPlayers()) {
            if (p.getName().equalsIgnoreCase(playerName)) return p;
        }
        return null;
    }

    public void displayPossibleWords() {
        String activeKey = gameSession.getActiveSortedKey();
        List<String> words = dictionaryMap.get(activeKey);
        if (words != null) {
            System.out.println("Possible words: " + words);
        }
    }

    // Called from GameView when next round begins
    public LetterSet startNextRound() {
        gameSession.nextRound();
        return generateLetters();
    }
}
