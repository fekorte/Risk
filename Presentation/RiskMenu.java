package Presentation;
import Business.IPlayerManager;

import javax.swing.*;
import java.awt.*;

public class RiskMenu extends JMenuBar{

    IPlayerManager playerManager;
    JLabel currentRound;
    //JLabel currentPlayerName;

    public RiskMenu(IPlayerManager playerManager, RiskView.RiskMenuListener listener) {

        this.playerManager = playerManager;
        setLayout(new GridLayout(1, 8));

        //JLabel currentPlayerName = new JLabel("Player: " + playerManager.getCurrentPlayerName());
        //add(currentPlayerName);

        currentRound = new JLabel("Round: " + playerManager.getRound());
        add(currentRound);

        String[] buttonLabels = {"Show country infos", "Show my mission", "Distribute units", "Done, continue", "Start new game", "Save game"};

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(listener);
            add(button);
        }
    }

    public void setButtonText(int buttonNumber, String buttonText){

        JButton button = (JButton) getComponent(buttonNumber);
        button.setText(buttonText);
    }

    public void nextRound(){

        currentRound.setText("Round: " + playerManager.getRound());
        //currentPlayerName.setText("Player: " + playerManager.getCurrentPlayerName());
    }
}


