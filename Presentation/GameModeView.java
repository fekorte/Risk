package Presentation;

import Business.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GameModeView extends JFrame {

    public GameModeView(IWorldManager worldManager) throws IOException {

        JPanel startPanel = new JPanel(new GridLayout(4, 1));

        JButton playStandardRiskButton = new JButton("Standard mode");
        JButton playMissionRiskButton = new JButton("Mission mode");
        JButton continueSavedRiskGameButton = new JButton("Continue saved risk game");
        JButton goBackButton = new JButton("Go back");
        startPanel.add(playStandardRiskButton);
        startPanel.add(playMissionRiskButton);
        startPanel.add(continueSavedRiskGameButton);
        startPanel.add(goBackButton);

        this.add(startPanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        IPlayerManager playerManager = new PlayerManager(worldManager);
        IGameManager gameManager = new GameManager(playerManager, worldManager);

        playStandardRiskButton.addActionListener(a -> {

            GameSetUpView gView = new GameSetUpView(worldManager, playerManager, gameManager, true);
            this.dispose();
        });

        playMissionRiskButton.addActionListener(a -> {

            GameSetUpView gView = new GameSetUpView(worldManager, playerManager, gameManager, false);
            this.dispose();
        });

        continueSavedRiskGameButton.addActionListener(a -> {

            try {
                if(playerManager.getContinuePreviousGame()){
                    RiskView rView = new RiskView(worldManager, playerManager, gameManager);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "No previous game state available.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        goBackButton.addActionListener(a -> {

            try {
                gameManager.newGame();
                StartView sView = new StartView();
                this.dispose();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
