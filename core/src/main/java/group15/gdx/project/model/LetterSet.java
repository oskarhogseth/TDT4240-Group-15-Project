package group15.gdx.project.model;

public class LetterSet {

    private final String scrambled;
    private final String sortedKey;

    public LetterSet(String scrambled, String sortedKey) {
        this.scrambled = scrambled;
        this.sortedKey = sortedKey;
    }

    public String getScrambled() { return scrambled; }
    public String getSortedKey() { return sortedKey; }
}
