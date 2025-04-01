package group15.gdx.project.api;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;
import java.time.Duration;

public class DictionaryApi {
    private static final String BASE_URL = "https://api.dictionaryapi.dev/api/";
    private static final String DEFAULT_VERSION = "v2"; // Adjust if needed

    private final HttpClient httpClient;
    private final String version;

    public DictionaryApi() {
        this(DEFAULT_VERSION);
    }

    public DictionaryApi(String version) {
        this.version = version;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    /**
     * Checks if the specified word exists in the dictionary.
     *
     * @param word the word to check
     * @return true if the word exists (HTTP 200), false otherwise
     */
    public boolean isWordValid(String word) {
        String url = BASE_URL + version + "/entries/en/" + word;
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Example usage:
    public static void main(String[] args) {
        DictionaryApi client = new DictionaryApi();
        String word = "example";  // Replace with dynamic input as needed
        if (client.isWordValid(word)) {
            System.out.println("Valid word!");
        } else {
            System.out.println("Invalid word or error occurred.");
        }
    }
}
