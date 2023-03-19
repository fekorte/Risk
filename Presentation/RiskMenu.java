package Presentation;

import Business.GameManager;
import Business.IPlayerManager;

import javax.swing.*;
import java.awt.*;

public class RiskMenu extends JMenuBar{

    public RiskMenu(IPlayerManager playerManager, GameManager gameManager, RiskView.RiskMenuListener listener) {

        setLayout(new GridLayout(1, 8));

        //JLabel currentPlayerName = new JLabel("Current player: " + playerManager.getCurrentPlayerName());
        //add(currentPlayerName);

        JLabel currentRound = new JLabel("Round: " + playerManager.getRound());
        add(currentRound);

        String[] buttonLabels = {"Save game", "Start new game", "Show country infos", "Show my mission", "Done, continue"};

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(listener);
            add(button);
        }
    }
}


