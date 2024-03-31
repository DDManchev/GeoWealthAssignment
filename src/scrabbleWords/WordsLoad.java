package scrabbleWords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads all words from the English language as a SET collection
 */
public class WordsLoad {

  private static final String WORDS_URL = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";

  public static Set<String> loadWordsFromSource() throws IOException {
    URL wordsUrl = new URL(WORDS_URL);

    try (var reader = new BufferedReader(
        new InputStreamReader(wordsUrl.openConnection().getInputStream()))) {
      Set<String> words = reader.lines().skip(2).collect(Collectors.toSet());
      return words;
    }
  }
}
