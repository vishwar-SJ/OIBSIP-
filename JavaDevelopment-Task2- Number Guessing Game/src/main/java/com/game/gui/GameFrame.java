package com.game.gui;

import com.game.Difficulty;
import com.game.GameEngine;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final GameEngine gameEngine;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final MenuPanel menuPanel;
    private final PlayPanel playPanel;

    public GameFrame() {
        super("Number Guessing Game");
        this.gameEngine = new GameEngine();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        this.menuPanel = new MenuPanel(this);
        this.playPanel = new PlayPanel(this, gameEngine);

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(playPanel, "PLAY");

        add(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "MENU");
    }

    public void startGame(Difficulty difficulty) {
        gameEngine.startNewRound(difficulty);
        playPanel.resetForNewRound();
        cardLayout.show(mainPanel, "PLAY");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            new GameFrame().setVisible(true);
        });
    }
}
