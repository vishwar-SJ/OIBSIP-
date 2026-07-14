package com.game.gui;

import com.game.Difficulty;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private final GameFrame gameFrame;

    public MenuPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255)); // Alice Blue

        JLabel titleLabel = new JLabel("Number Guessing Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 50, 100));
        buttonPanel.setOpaque(false);

        JButton easyBtn = createDifficultyButton("Easy (1-50, 10 attempts)", Difficulty.EASY);
        JButton mediumBtn = createDifficultyButton("Medium (1-100, 7 attempts)", Difficulty.MEDIUM);
        JButton hardBtn = createDifficultyButton("Hard (1-200, 5 attempts)", Difficulty.HARD);

        buttonPanel.add(easyBtn);
        buttonPanel.add(mediumBtn);
        buttonPanel.add(hardBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createDifficultyButton(String text, Difficulty difficulty) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> gameFrame.startGame(difficulty));
        return button;
    }
}
