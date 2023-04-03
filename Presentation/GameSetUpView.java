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

    private final IPlayerManager playerManager;
    private final IGameManager gameManager;
    private final IWorldManager worldManager;
    private final boolean standardRisk;
    private JLabel playerInfo;
    private JPanel setUpPanel;
    private JButton addPlayerButton;
    private JButton removePlayerButton;
    private JButton startGameButton;
    private JButton goBackButton;

    public GameSetUpView(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager, boolean standardRisk){

        this.playerManager = playerManager;
        this.gameManager = gameManager;
        this.worldManager = worldManager;
        this.standardRisk = standardRisk;

        setUp();
    }

    private void setUp(){

        this.playerInfo = new JLabel();
        this.setUpPanel = new JPanel(new GridLayout(5, 1));
        setUpPanel.add(playerInfo);

        createButtons();
        setUpAddPlayerButton();
        deletePreviousGameState();
        removePlayerButton();
        startGameButton();

        setFrameAttributes();
    }

    private void setFrameAttributes(){

        this.add(setUpPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    private void deletePreviousGameState(){

        try {
            gameManager.newGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createButtons(){

        addPlayerButton = new JButton("Add player");
        removePlayerButton = new JButton("Remove player");
        startGameButton = new JButton("Start game");
        goBackButton = new JButton("Go back");

        addButtons();
    }

    private void addButtons(){

        setUpPanel.add(addPlayerButton);
        setUpPanel.add(removePlayerButton);
        setUpPanel.add(startGameButton);
        setUpPanel.add(goBackButton);
    }

    private void setUpAddPlayerButton(){

        addPlayerButton.addActionListener(a -> {
            JFrame addPlayerFrame = new JFrame("New player");
            JPanel addPlayerPanel = new JPanel(new GridLayout(0, 1));
            JLabel nameLabel = new JLabel("Name: ");
            JTextField nameField = new JTextField();
            addPlayerPanel.add(nameLabel);
            addPlayerPanel.add(nameField);

            ButtonGroup group = new ButtonGroup();
            for (String color : playerManager.getAllowedColors()) {
                JRadioButton button = new JRadioButton(color);
                group.add(button);
                addPlayerPanel.add(button);
            }


            addPlayerDoneButton(addPlayerFrame, addPlayerPanel, nameField, group);

            addPlayerFrame.dispose();
        });
    }

    private void addPlayerDoneButton(JFrame addPlayerFrame, JPanel addPlayerPanel, JTextField nameField, ButtonGroup group){

        JButton doneButton = new JButton("Done");
        addPlayerPanel.add(doneButton);
        addPlayerFrame.add(addPlayerPanel);
        addPlayerFrame.pack();
        addPlayerFrame.setSize(400, 300);
        addPlayerFrame.setLocationRelativeTo(null);
        addPlayerFrame.setVisible(true);

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

            } catch (ExceptionEmptyInput | ExceptionPlayerAlreadyExists | ExceptionTooManyPlayers |
                     ExceptionColorAlreadyExists c){
                JOptionPane.showMessageDialog(null, c.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }


    private void removePlayerButton(){

        removePlayerButton.addActionListener(a -> {

            String playerToRemove = JOptionPane.showInputDialog(null, "Player name: ", "Remove player", JOptionPane.INFORMATION_MESSAGE);
            try{
                playerManager.removePlayer(playerToRemove);
                playerInfo.setText(playerManager.getPlayersInfo());
                JOptionPane.showMessageDialog(null, playerToRemove + " has been removed successfully.", "Player added", JOptionPane.INFORMATION_MESSAGE);
            } catch(ExceptionEmptyInput | ExceptionObjectDoesntExist e){
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }


    private void startGameButton(){

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
    }


    private void goBackButton(){

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
