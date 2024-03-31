package scrabbleWords;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

  public static void main(String[] args) {
    try {
      Set<String> allWords = WordsLoad.loadWordsFromSource();
      long startTime = System.currentTimeMillis();
      Set<String> result = buildUpWords(allWords);
      System.out.println("9 letter valid words count: " + result.size());
      System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime) + " ms");
    } catch (IOException e) {
      System.err.println("Failed to load words: " + e.getMessage());
    }
  }

  private static Set<String> buildUpWords(Set<String> allWords) {
    Set<String> validWords = new HashSet<>(Set.of("A", "I"));
    Set<String> currentWords = new HashSet<>(validWords);

    ExecutorService executor = Executors.newWorkStealingPool();
    try {
      for (int length = 2; length <= 9; length++) {
        Set<String> nextWords = ConcurrentHashMap.newKeySet();
        for (String word : currentWords) {
          executor.submit(() -> {
            for (char letter = 'A'; letter <= 'Z'; letter++) {
              for (int position = 0; position <= word.length(); position++) {
                String newWord = new StringBuilder(word).insert(position, letter).toString();
                if (allWords.contains(newWord)) {
                  nextWords.add(newWord);
                }
              }
            }
          });
        }
        // All task must be completed before moving to next iteration
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        executor = Executors.newWorkStealingPool(); // Reinitialize for the next iteration

        currentWords = nextWords;
        validWords.addAll(nextWords);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Thread was interrupted: " + e.getMessage());
    } finally {
      if (!executor.isShutdown()) {
        executor.shutdownNow();
      }
    }

    return validWords.stream().filter(word -> word.length() == 9).collect(Collectors.toSet());
  }
}






