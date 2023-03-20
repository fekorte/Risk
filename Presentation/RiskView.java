package Presentation;

import Business.IGameManager;
import Business.IPlayerManager;
import Business.IWorldManager;
import Common.Exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class RiskView extends JFrame implements RiskBoardPanel.RiskBoardListener {

    private final IPlayerManager playerManager;
    private final IWorldManager worldManager;
    private final IGameManager gameManager;
    private final Object lock;
    private String selectedCountry;
    private int gameStep;
    boolean gameStarted;
    boolean riskVersionSelected;
    boolean gameSetUp;

    public RiskView(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager) throws IOException {

        this.worldManager = worldManager;
        this.playerManager = playerManager;
        this.gameManager = gameManager;
        this.lock = new Object();

        if(playerManager.continuePreviousGame()){
            gameStarted = true;
            riskVersionSelected = true;
            gameSetUp = true;
            gameStep = gameManager.getSavedGameStep();
            switch(gameStep){
                case(1) -> {
                    receiveUnits();
                }//action button and done,continue-button invisible,
                case (2) -> {
                    //set action button to "attack" name
                }
                case(3) -> {} //set action button to "move units"
            }
        } else {
            gameStarted = false;
            riskVersionSelected = false;
            gameSetUp = false;
            gameStep = 0;
            //receiveUnits();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);
        setLayout(new BorderLayout());

        RiskBoardPanel boardPanel = new RiskBoardPanel(worldManager, this);
        add(boardPanel, BorderLayout.CENTER);
        RiskMenu riskMenu = new RiskMenu(playerManager, new RiskMenuListener());
        add(riskMenu, BorderLayout.NORTH);
        setVisible(true);
    }

    private String waitForCountrySelection() {
        synchronized (lock) {
            while (selectedCountry == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String countryName = selectedCountry;
            selectedCountry = null;
            return countryName;
        }
    }

    public void onCountrySelected(String countryName) {
        synchronized (lock) {
            this.selectedCountry = countryName;
            lock.notify();
        }
    }

    private void receiveUnits(){

        gameStep = 0;
        int receivedUnits = 0;
        try{
            receivedUnits = gameManager.receiveUnits();
        } catch(ExceptionObjectDoesntExist e){
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "New round! It's your turn " + playerManager.getCurrentPlayerName() + "You receive " + receivedUnits + " units." + "Click 'done, continue' in the menu to distribute your received units to your countries.", playerManager.getCurrentPlayerName() + "'s turn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void distributeUnits(int receivedUnits){

    }
    private void attack(){

        JOptionPane.showMessageDialog(null, "Please click on the country to attack from.", "Select country", JOptionPane.INFORMATION_MESSAGE);
        String attackingCountry = waitForCountrySelection();

        JOptionPane.showMessageDialog(null, "Country successfully selected. Please click on the country you want to attack.", "Select country", JOptionPane.INFORMATION_MESSAGE);
        String attackedCountry = waitForCountrySelection();

        String units = JOptionPane.showInputDialog(null, "Country successfully selected. Enter unit amount (select max. 3 and keep in mind that one unit has to remain in your country)", "Select units", JOptionPane.INFORMATION_MESSAGE);

        try{
            List<Integer> attackerDiceResult = gameManager.attack(attackingCountry, attackedCountry, Integer.parseInt(units));
            JOptionPane.showMessageDialog(null, attackingCountry + " has attacked " + attackedCountry + ". " + playerManager.getCurrentPlayerName() + " you've rolled " + attackerDiceResult, "Dice result" , JOptionPane.INFORMATION_MESSAGE);

            defend(attackingCountry, attackedCountry, Integer.parseInt(units), attackerDiceResult);
        } catch(ExceptionCountryNotRecognized | ExceptionCountryNotOwned | ExceptionCountryIsNoNeighbour | NumberFormatException |
                ExceptionTooLessUnits | ExceptionTooManyUnits | IOException e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            attack();
        }
    }

    private void defend(String attackingCountry, String attackedCountry, int unitsFromAttacker, List<Integer> attackerDiceResult) throws IOException {

        String defenderName = worldManager.getCountryOwner(attackedCountry);
        JOptionPane.showMessageDialog(null, "Your country has been attacked " + defenderName + "! You have to defend it!", "Defend country", JOptionPane.INFORMATION_MESSAGE);
        List<Integer> defenderDiceResult = gameManager.defend(attackedCountry, attackingCountry, attackerDiceResult, unitsFromAttacker);

        JOptionPane.showMessageDialog(null, defenderName + " rolled " + defenderDiceResult + " and " + playerManager.getCurrentPlayerName() + " rolled " + attackerDiceResult + ". ", "Dice results", JOptionPane.INFORMATION_MESSAGE);


        if (!worldManager.getCountryOwner(attackedCountry).equals(playerManager.getCurrentPlayerName())) {
            JOptionPane.showMessageDialog(null, defenderName + " was able to defend " + attackedCountry + ". " + worldManager.getUnitAmountOfCountry(attackedCountry) + " units remain in " + attackedCountry + " and "
                    + worldManager.getUnitAmountOfCountry(attackingCountry) + " units remain in " + attackingCountry, attackedCountry + " defended", JOptionPane.INFORMATION_MESSAGE);
        }

        int moveUnitsDecision = JOptionPane.showOptionDialog(null,
                playerManager.getCurrentPlayerName() + " was able to conquer " + attackedCountry + ". " + attackingCountry + " unit amount: " + worldManager.getUnitAmountOfCountry(attackingCountry) +
                        ". Current unit amount in " + attackedCountry + ": " + worldManager.getUnitAmountOfCountry(attackedCountry) + "." + playerManager.getCurrentPlayerName() + " do you want to move additional units to " + attackedCountry + "?",

                attackedCountry + " conquered", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);

        boolean confirmed = false;
        while (!confirmed) {
            if (moveUnitsDecision == JOptionPane.YES_OPTION){
                String units = JOptionPane.showInputDialog(null, "Please note that at least one unit has to remain in " + attackingCountry, "Select units", JOptionPane.INFORMATION_MESSAGE);
                try {
                    gameManager.moveUnits(attackingCountry, attackedCountry, Integer.parseInt(units), true);
                    confirmed = true;
                } catch (ExceptionCountryNotRecognized | ExceptionInvolvedCountrySelected | ExceptionCountryNotOwned |
                         ExceptionTooManyUnits | ExceptionCountryIsNoNeighbour e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        JOptionPane.showMessageDialog(null, worldManager.getUnitAmountOfCountry(attackedCountry) + " units remain in " + attackedCountry + " and "
                + worldManager.getUnitAmountOfCountry(attackingCountry) + " units remain in " + attackingCountry, "Result of fight", JOptionPane.INFORMATION_MESSAGE);

        if (playerManager.playerDefeated(defenderName)) {
            JOptionPane.showMessageDialog(null, defenderName + " your last country has been conquered, the game has to continue without you. ", defenderName + " lost", JOptionPane.INFORMATION_MESSAGE);
            try {
                playerManager.removePlayer(defenderName);
            } catch (ExceptionObjectDoesntExist e) {
                e.printStackTrace();
            }

            if (playerManager.getPlayerNumber() == 1) {
                System.out.println();
                JOptionPane.showMessageDialog(null, playerManager.getCurrentPlayerName() + " congratulation, you've won!", playerManager.getCurrentPlayerName() + " won", JOptionPane.INFORMATION_MESSAGE);

                gameStarted = false;
                gameSetUp = false;
                gameManager.quitGame();
            }
        }
    }


    private void moveUnits() throws IOException {

        JOptionPane.showMessageDialog(null, "Please click on the country from whom you want to select units. Keep in mind that you cannot move units from a country which has already been involved in this round.", "Select country", JOptionPane.INFORMATION_MESSAGE);
        String sourceCountry = waitForCountrySelection();

        JOptionPane.showMessageDialog(null, "Country successfully selected. Please click on the country to whom you want to move your units. Keep in mind that you cannot move units to a country which has already been involved in this round.", "Select country", JOptionPane.INFORMATION_MESSAGE);
        String destinationCountry = waitForCountrySelection();

        String units = JOptionPane.showInputDialog(null, "Country successfully selected. Enter unit amount (at least one unit has to remain in each country)", "Select units", JOptionPane.INFORMATION_MESSAGE);

        try{
            gameManager.moveUnits(sourceCountry, destinationCountry, Integer.parseInt(units), false);
        } catch(ExceptionCountryNotRecognized | ExceptionInvolvedCountrySelected | ExceptionCountryNotOwned | ExceptionTooManyUnits | ExceptionCountryIsNoNeighbour e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }



    class RiskMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "Show country infos" -> {} //create table to display country infos
                case "Show my mission" -> JOptionPane.showMessageDialog(null, playerManager.getCurrentPlayerName() + "don't share your mission with anyone else! Your mission is: " + playerManager.getCurrentPlayerMission(), "Your mission", JOptionPane.INFORMATION_MESSAGE);
                case "Attack" -> attack();
                case "Move units" -> {
                    try {
                        moveUnits();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                case "Distribute units" -> {} //distributeUnits();
                case "Done, continue" -> gameStep++;
                case "Start new game" -> {
                    gameStarted = false;
                    gameSetUp = false;

                    System.out.println(" Y/N > ");
                    int decisionDelete = JOptionPane.showOptionDialog(null,"Previously saved data will be deleted. Do you want to continue?",
                            "Start new game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);

                    if (decisionDelete == JOptionPane.YES_OPTION){
                        try {
                            gameManager.newGame();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

                case "Save game" -> {
                    try {
                        if (gameManager.saveGame(gameStep)) {
                            JOptionPane.showMessageDialog(null, "Game saved successfully!", "Game saved", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }
}
