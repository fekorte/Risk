package Presentation;

import Business.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class StartView extends JFrame{
    public StartView() throws IOException {

        IWorldManager worldManager = new WorldManager();

        JPanel startPanel = new JPanel(new GridLayout(2, 1));

        JButton playStandardRiskButton = new JButton("Standard risk");
        JButton playLOTRRiskButton = new JButton("Lord of the Rings risk");
        startPanel.add(playStandardRiskButton);
        startPanel.add(playLOTRRiskButton);

        this.add(startPanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(600, 300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        playStandardRiskButton.addActionListener(a -> {

            try {
                worldManager.setWorldVersion("Standard Risk");
                GameModeView mView = new GameModeView(worldManager);
                this.dispose();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        playLOTRRiskButton.addActionListener(a -> {

            try {
                worldManager.setWorldVersion("LOTR risk");
                GameModeView mView = new GameModeView(worldManager);
                this.dispose();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
