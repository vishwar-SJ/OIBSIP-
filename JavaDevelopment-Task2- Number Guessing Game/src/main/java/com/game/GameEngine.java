package com.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {
    private Difficulty difficulty;
    private int targetNumber;
    private int currentAttempts;
    private int roundsPlayed;
    private List<String> scoreHistory;
    private final Random random;

    public GameEngine() {
        this.random = new Random();
        this.scoreHistory = new ArrayList<>();
        this.roundsPlayed = 0;
    }

    public void startNewRound(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.currentAttempts = 0;
        this.roundsPlayed++;
        // Generate number between 1 and maxNumber (inclusive)
        this.targetNumber = random.nextInt(difficulty.getMaxNumber()) + 1;
    }

    public enum GuessResult {
        TOO_HIGH, TOO_LOW, CORRECT, GAME_OVER_LOST
    }

    public GuessResult processGuess(int guess) {
        currentAttempts++;

        if (guess == targetNumber) {
            recordScore(true);
            return GuessResult.CORRECT;
        }

        if (currentAttempts >= difficulty.getMaxAttempts()) {
            recordScore(false);
            return GuessResult.GAME_OVER_LOST;
        }

        return guess > targetNumber ? GuessResult.TOO_HIGH : GuessResult.TOO_LOW;
    }

    private void recordScore(boolean won) {
        if (won) {
            scoreHistory.add("Round " + roundsPlayed + " — guessed in " + currentAttempts + " attempts (" + difficulty.name() + ")");
        } else {
            scoreHistory.add("Round " + roundsPlayed + " — Lost (" + difficulty.name() + ")");
        }
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public int getCurrentAttempts() {
        return currentAttempts;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<String> getScoreHistory() {
        return scoreHistory;
    }
}
