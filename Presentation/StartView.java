package Presentation;

import Business.IGameManager;
import Business.IPlayerManager;
import Business.IWorldManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class StartView extends JFrame{

    public StartView(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager){

        JPanel startPanel = new JPanel(new GridLayout(3, 1));

        JButton playStandardRiskButton = new JButton("Play standard risk");
        JButton playMissionRiskButton = new JButton("Play mission risk");
        JButton continueSavedRiskGameButton = new JButton("Continue saved risk game");
        startPanel.add(playStandardRiskButton);
        startPanel.add(playMissionRiskButton);
        startPanel.add(continueSavedRiskGameButton);

        this.add(startPanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

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
                if(playerManager.continuePreviousGame()){
                    RiskView rView = new RiskView(worldManager, playerManager, gameManager);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "No previous game state available.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
