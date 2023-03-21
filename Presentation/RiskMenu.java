package Presentation;
import Business.IPlayerManager;

import javax.swing.*;
import java.awt.*;

public class RiskMenu extends JMenuBar{

    IPlayerManager playerManager;
    JLabel currentInfo;

    public RiskMenu(IPlayerManager playerManager, RiskView.RiskMenuListener listener) {

        this.playerManager = playerManager;
        setLayout(new GridLayout(1, 8));

        currentInfo = new JLabel("Round: " + playerManager.getRound() + ", Player: " + playerManager.getCurrentPlayerName());
        add(currentInfo);

        String[] buttonLabels = {"Show country infos", "Show my mission", "Distribute units", "Start new game", "Save game"};

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

    public void updateInfoLabel(){

        currentInfo.setText("Round: " + playerManager.getRound() + ", Player: " + playerManager.getCurrentPlayerName());
    }
}


