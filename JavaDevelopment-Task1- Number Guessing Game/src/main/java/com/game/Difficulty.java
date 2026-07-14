package com.game;

public enum Difficulty {
    EASY(50, 10),
    MEDIUM(100, 7),
    HARD(200, 5);

    private final int maxNumber;
    private final int maxAttempts;

    Difficulty(int maxNumber, int maxAttempts) {
        this.maxNumber = maxNumber;
        this.maxAttempts = maxAttempts;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }
}
