package Presentation;

import Business.IGameManager;
import Business.IPlayerManager;
import Business.IWorldManager;
import Common.Exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Iterator;

public class GameSetUpView extends JFrame{

    public GameSetUpView(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager, boolean standardRisk){

        try {
            gameManager.newGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JPanel setUpPanel = new JPanel(new GridLayout(5, 1));

        JLabel playerInfo = new JLabel();
        setUpPanel.add(playerInfo);

        JButton addPlayerButton = new JButton("Add player");
        JButton removePlayerButton = new JButton("Remove player");
        JButton startGameButton = new JButton("Start game");
        JButton goBackButton = new JButton("Go back");
        setUpPanel.add(addPlayerButton);
        setUpPanel.add(removePlayerButton);
        setUpPanel.add(startGameButton);
        setUpPanel.add(goBackButton);

        this.add(setUpPanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);


        addPlayerButton.addActionListener(a -> {
            JFrame frame = new JFrame("New player");
            JPanel panel = new JPanel(new GridLayout(0, 1));
            JLabel nameLabel = new JLabel("Name: ");
            JTextField nameField = new JTextField();
            panel.add(nameLabel);
            panel.add(nameField);

            ButtonGroup group = new ButtonGroup();
            for (String color : playerManager.getAllowedColors()) {
                JRadioButton button = new JRadioButton(color);
                group.add(button);
                panel.add(button);
            }

            JButton doneButton = new JButton("Done");

            panel.add(doneButton);
            frame.add(panel);
            frame.pack();
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);


            doneButton.addActionListener(e -> {
                String playerName = nameField.getText();
                String playerColor = "";
                for (Iterator<AbstractButton> buttons = group.getElements().asIterator(); buttons.hasNext();) {
                    AbstractButton button = buttons.next();
                    if (button.isSelected()) {
                        playerColor = button.getText();
                        break;
                    }
                }
                try{
                    playerManager.addPlayer(playerName, playerColor);
                    playerInfo.setText(playerManager.getPlayersInfo());
                    JOptionPane.showMessageDialog(null, playerName + " has been added.", "Player added", JOptionPane.INFORMATION_MESSAGE);

                } catch (ExceptionEmptyInput | ExceptionPlayerAlreadyExists | ExceptionTooManyPlayer |
                         ExceptionColorAlreadyExists c){
                    JOptionPane.showMessageDialog(null, c.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);

                }
                frame.dispose();
            });
        });

        removePlayerButton.addActionListener(a -> {

            String playerToRemove = JOptionPane.showInputDialog(null, "Player name: ", "Remove player", JOptionPane.INFORMATION_MESSAGE);
            try{
                playerManager.removePlayer(playerToRemove);
                JOptionPane.showMessageDialog(null, playerToRemove + " has been removed successfully.", "Player added", JOptionPane.INFORMATION_MESSAGE);
            } catch(ExceptionEmptyInput | ExceptionObjectDoesntExist e){
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        startGameButton.addActionListener(a -> {

            try{
                gameManager.startFirstRound(standardRisk);
                RiskView rView = new RiskView(worldManager, playerManager, gameManager);
                this.dispose();
            } catch (ExceptionNotEnoughPlayer e){
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        goBackButton.addActionListener(a -> {

            try {
                gameManager.newGame();
                StartView sView = new StartView(worldManager, playerManager, gameManager);
                this.dispose();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
