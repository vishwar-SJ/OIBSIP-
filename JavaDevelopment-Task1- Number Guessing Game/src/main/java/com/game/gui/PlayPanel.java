package com.game.gui;

import com.game.Difficulty;
import com.game.GameEngine;
import com.game.GameEngine.GuessResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PlayPanel extends JPanel {
    private final GameFrame gameFrame;
    private final GameEngine gameEngine;

    private JLabel instructionLabel;
    private JTextField guessField;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel attemptLabel;
    private JTextArea scoreArea;

    public PlayPanel(GameFrame gameFrame, GameEngine gameEngine) {
        this.gameFrame = gameFrame;
        this.gameEngine = gameEngine;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Top Panel for Instructions and feedback
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        instructionLabel = new JLabel("", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        attemptLabel = new JLabel("", SwingConstants.CENTER);
        attemptLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        feedbackLabel = new JLabel("Enter your first guess!", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        feedbackLabel.setForeground(Color.BLUE);

        topPanel.add(instructionLabel);
        topPanel.add(attemptLabel);
        topPanel.add(feedbackLabel);

        add(topPanel, BorderLayout.NORTH);

        // Center panel for input
        JPanel centerPanel = new JPanel(new FlowLayout());
        centerPanel.setOpaque(false);
        
        guessField = new JTextField(10);
        guessField.setFont(new Font("Arial", Font.PLAIN, 18));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processGuess();
                }
            }
        });

        submitButton = new JButton("Guess");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.addActionListener(e -> processGuess());

        centerPanel.add(guessField);
        centerPanel.add(submitButton);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for scores and menu
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scoreArea = new JTextArea(5, 30);
        scoreArea.setEditable(false);
        scoreArea.setFocusable(false);
        scoreArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(scoreArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Round History"));

        JButton menuButton = new JButton("Back to Menu");
        menuButton.addActionListener(e -> gameFrame.showMenu());

        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(menuButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void resetForNewRound() {
        Difficulty diff = gameEngine.getDifficulty();
        instructionLabel.setText("Guess a number between 1 and " + diff.getMaxNumber());
        updateAttemptLabel();
        feedbackLabel.setText("Good luck!");
        feedbackLabel.setForeground(Color.BLUE);
        guessField.setText("");
        guessField.setEnabled(true);
        submitButton.setEnabled(true);
        updateScoreArea();
        guessField.requestFocus();
    }

    private void processGuess() {
        String text = guessField.getText().trim();
        if (text.isEmpty()) return;

        try {
            int guess = Integer.parseInt(text);
            GuessResult result = gameEngine.processGuess(guess);
            updateAttemptLabel();

            switch (result) {
                case TOO_HIGH:
                    feedbackLabel.setText("Too High!");
                    feedbackLabel.setForeground(Color.RED);
                    break;
                case TOO_LOW:
                    feedbackLabel.setText("Too Low!");
                    feedbackLabel.setForeground(new Color(200, 100, 0)); // Orange
                    break;
                case CORRECT:
                    feedbackLabel.setText("Correct! You win!");
                    feedbackLabel.setForeground(new Color(0, 150, 0)); // Green
                    endRound(true);
                    break;
                case GAME_OVER_LOST:
                    feedbackLabel.setText("You Lost! The number was " + gameEngine.getTargetNumber());
                    feedbackLabel.setForeground(Color.RED);
                    endRound(false);
                    break;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
        guessField.setText("");
        guessField.requestFocus();
    }

    private void updateAttemptLabel() {
        Difficulty diff = gameEngine.getDifficulty();
        attemptLabel.setText("Attempts: " + gameEngine.getCurrentAttempts() + " / " + diff.getMaxAttempts());
    }

    private void updateScoreArea() {
        StringBuilder sb = new StringBuilder();
        for (String record : gameEngine.getScoreHistory()) {
            sb.append(record).append("\n");
        }
        scoreArea.setText(sb.toString());
    }

    private void endRound(boolean won) {
        guessField.setEnabled(false);
        submitButton.setEnabled(false);
        updateScoreArea();

        String message = won ? "Congratulations, you won!" : "Game Over! The number was " + gameEngine.getTargetNumber();
        int choice = JOptionPane.showConfirmDialog(this, message + "\nDo you want to play again?", "Round Over", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            gameFrame.startGame(gameEngine.getDifficulty());
        } else {
            gameFrame.showMenu();
        }
    }
}
