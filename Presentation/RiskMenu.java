package Presentation;
import Business.IPlayerManager;

import javax.swing.*;
import java.awt.*;

public class RiskMenu extends JMenuBar{
    private final IPlayerManager playerManager;
    private final JLabel currentGameInfo;

    public RiskMenu(IPlayerManager playerManager, RiskView.RiskMenuListener listener) {

        this.playerManager = playerManager;
        setLayout(new GridLayout(1, 8));

        currentGameInfo = new JLabel("Round: " + playerManager.getRound() + ", Player: " + playerManager.getCurrentPlayerName());
        add(currentGameInfo);

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
    public void updateInfoLabel(){

        currentGameInfo.setText("Round: " + playerManager.getRound() + ", Player: " + playerManager.getCurrentPlayerName());
    }
}


