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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RiskView extends JFrame implements RiskBoardPanel.RiskBoardListener {

    private final IPlayerManager playerManager;
    private final IWorldManager worldManager;
    private final IGameManager gameManager;
    private CompletableFuture<String> countrySelectedFuture;
    private final Map<String, RiskPlayerPanel> playerPanelMap;
    private final RiskMenu riskMenu;
    private int gameStep;

    public RiskView(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager) throws IOException {

        this.worldManager = worldManager;
        this.playerManager = playerManager;
        this.gameManager = gameManager;
        countrySelectedFuture = new CompletableFuture<>();
        this.playerPanelMap = new HashMap<>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);
        setLayout(new BorderLayout());

        RiskBoardPanel boardPanel = new RiskBoardPanel(worldManager, this);
        add(boardPanel, BorderLayout.CENTER);
        riskMenu = new RiskMenu(playerManager, new RiskMenuListener());
        add(riskMenu, BorderLayout.NORTH);

        initializeRiskPlayerPanel();
        setVisible(true);

        checkPreviousGameState();
    }


    private void initializeRiskPlayerPanel(){

        RiskPlayerPanel leftPanel = new RiskPlayerPanel(playerManager);
        RiskPlayerPanel rightPanel = new RiskPlayerPanel(playerManager);

        List<String> playerNames = playerManager.getPlayerNames();
        for (int i = 0; i < playerManager.getPlayerNumber(); i++) {
            String playerName = playerNames.get(i);
            if (i % 2 == 0){
                leftPanel.addPlayerCountryList(playerName);
                playerPanelMap.put(playerName, leftPanel);
            } else {
                rightPanel.addPlayerCountryList(playerName);
                playerPanelMap.put(playerName, rightPanel);
            }
        }

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private void checkPreviousGameState() throws IOException {

        gameStep = (playerManager.continuePreviousGame()) ? gameManager.getSavedGameStep() : 1;
        setActionButton();

        if(gameStep == 1){
            receiveUnits();
        }
    }

    private void setActionButton(){

        switch (gameStep){
            case(1) -> {
                riskMenu.setButtonText(3, "Distribute units");
                riskMenu.updateInfoLabel();
            }
            case(2) -> riskMenu.setButtonText(3, "Attack");
            case(3) -> riskMenu.setButtonText(3, "Move units");
        }
    }

    public void onCountrySelected(String countryName) {

        System.out.println("Selected country: " + countryName);
        this.countrySelectedFuture.complete(countryName);
    }

    private void receiveUnits(){

        int receivedUnits = 0;
        try{
            receivedUnits = gameManager.receiveUnits();
        } catch(ExceptionObjectDoesntExist e){
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "It's your turn " + playerManager.getCurrentPlayerName() + ". \n You receive " + receivedUnits + " units. \n Click 'Distribute units' in the menu to distribute your received units to your countries.", playerManager.getCurrentPlayerName() + "'s turn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void distributeUnits() {
        JOptionPane.showMessageDialog(null, "You received " + gameManager.getReceivedUnits() + ". \n Please click on the country on which you want to distribute your units. ", "Distribute units", JOptionPane.INFORMATION_MESSAGE);
        countrySelectedFuture = new CompletableFuture<>();

        new Thread(() -> {
            try {
                while(gameManager.allUnitsDistributed()){
                    String selectedCountry = countrySelectedFuture.get();
                    countrySelectedFuture = new CompletableFuture<>();
                    String units = JOptionPane.showInputDialog(null, "Enter unit amount", "Select unit amount", JOptionPane.INFORMATION_MESSAGE);

                    try {
                        gameManager.distributeUnits(selectedCountry, Integer.parseInt(units));
                        JOptionPane.showMessageDialog(null,  Integer.parseInt(units) + " have been moved to " + selectedCountry + ". You have " + gameManager.getReceivedUnits()  + " left.", "Distribute units", JOptionPane.INFORMATION_MESSAGE);
                        playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
                    } catch (NumberFormatException f) {
                        JOptionPane.showMessageDialog(null, "Invalid unit amount selected.", "Error", JOptionPane.INFORMATION_MESSAGE);
                    } catch (ExceptionEmptyInput | ExceptionCountryNotRecognized | ExceptionCountryNotOwned | ExceptionTooManyUnits e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            gameStep++;
            setActionButton();
        }).start();
    }
    private void attack() throws InterruptedException, ExecutionException{

        countrySelectedFuture = new CompletableFuture<>();

        new Thread(() -> {
            try {
                JOptionPane.showMessageDialog(null, "Please click on the country to attack from.", "Select country", JOptionPane.INFORMATION_MESSAGE);
                String attackingCountry = countrySelectedFuture.get();
                countrySelectedFuture = new CompletableFuture<>();

                JOptionPane.showMessageDialog(null, "Please click on the country you want to attack.", "Select country", JOptionPane.INFORMATION_MESSAGE);
                String attackedCountry = countrySelectedFuture.get();
                countrySelectedFuture = new CompletableFuture<>();

                String units = JOptionPane.showInputDialog(null, "Enter unit amount (select max. 3 and keep in mind that one unit has to remain in your country)", "Select units", JOptionPane.INFORMATION_MESSAGE);

                try {
                    List<Integer> attackerDiceResult = gameManager.attack(attackingCountry, attackedCountry, Integer.parseInt(units));
                    JOptionPane.showMessageDialog(null, attackingCountry + " has attacked " + attackedCountry + ". " + playerManager.getCurrentPlayerName() + " you've rolled " + attackerDiceResult, "Dice result", JOptionPane.INFORMATION_MESSAGE);

                    defend(attackingCountry, attackedCountry, Integer.parseInt(units), attackerDiceResult);
                } catch (NumberFormatException f) {
                    JOptionPane.showMessageDialog(null, "Invalid unit amount selected.", "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch(ExceptionCountryNotRecognized | ExceptionEmptyInput | ExceptionCountryNotOwned | ExceptionCountryIsNoNeighbour |
                        ExceptionTooLessUnits | ExceptionTooManyUnits | IOException e){
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                    attack();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void defend(String attackingCountry, String attackedCountry, int unitsFromAttacker, List<Integer> attackerDiceResult) throws IOException {

        String defenderName = worldManager.getCountryOwner(attackedCountry);
        JOptionPane.showMessageDialog(null, "Your country has been attacked " + defenderName + "! You have to defend it!", "Defend country", JOptionPane.INFORMATION_MESSAGE);
        List<Integer> defenderDiceResult = gameManager.defend(attackedCountry, attackingCountry, attackerDiceResult, unitsFromAttacker);

        JOptionPane.showMessageDialog(null, defenderName + " rolled " + defenderDiceResult + " and " + playerManager.getCurrentPlayerName() + " rolled " + attackerDiceResult + ". ", "Dice results", JOptionPane.INFORMATION_MESSAGE);


        playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
        playerPanelMap.get(defenderName).updateList(defenderName);

        if (!worldManager.getCountryOwner(attackedCountry).equals(playerManager.getCurrentPlayerName())) {

            JOptionPane.showMessageDialog(null, defenderName + " was able to defend " + attackedCountry + ".\n" + worldManager.getUnitAmountOfCountry(attackedCountry) + " units remain in " + attackedCountry + " and "
                    + worldManager.getUnitAmountOfCountry(attackingCountry) + " units remain in " + attackingCountry, attackedCountry + " defended", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int moveUnitsDecision = JOptionPane.showOptionDialog(null,
                playerManager.getCurrentPlayerName() + " was able to conquer " + attackedCountry + ".\n" + attackingCountry + " unit amount: " + worldManager.getUnitAmountOfCountry(attackingCountry) +
                        ". Current unit amount in " + attackedCountry + ": " + worldManager.getUnitAmountOfCountry(attackedCountry) + ". \n" + playerManager.getCurrentPlayerName() + " do you want to move additional units to " + attackedCountry + "?",
                attackedCountry + " conquered", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);

        boolean confirmed = false;
        while (!confirmed) {
            if (moveUnitsDecision == JOptionPane.YES_OPTION){
                String units = JOptionPane.showInputDialog(null, "Please note that at least one unit has to remain in " + attackingCountry, "Select units", JOptionPane.INFORMATION_MESSAGE);
                try {
                    gameManager.moveUnits(attackingCountry, attackedCountry, Integer.parseInt(units), true);
                    confirmed = true;
                } catch (ExceptionCountryNotRecognized | ExceptionEmptyInput | ExceptionInvolvedCountrySelected | ExceptionCountryNotOwned |
                         ExceptionTooManyUnits | ExceptionCountryIsNoNeighbour e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        JOptionPane.showMessageDialog(null, worldManager.getUnitAmountOfCountry(attackedCountry) + " units remain in " + attackedCountry + " and "
                + worldManager.getUnitAmountOfCountry(attackingCountry) + " units remain in " + attackingCountry, "Result of fight", JOptionPane.INFORMATION_MESSAGE);

        if (playerManager.playerDefeated(defenderName)){
            JOptionPane.showMessageDialog(null, defenderName + " your last country has been conquered, the game has to continue without you. ", defenderName + " lost", JOptionPane.INFORMATION_MESSAGE);
            try {
                playerManager.removePlayer(defenderName);
            } catch (ExceptionEmptyInput | ExceptionObjectDoesntExist e) {
                e.printStackTrace();
            }
            if (playerManager.getPlayerNumber() == 1) {
                System.out.println();
                JOptionPane.showMessageDialog(null, playerManager.getCurrentPlayerName() + " congratulation, you've won!", playerManager.getCurrentPlayerName() + " won", JOptionPane.INFORMATION_MESSAGE);
                gameManager.quitGame();
            }
        }
    }


    private void moveUnits() throws IOException, InterruptedException, ExecutionException {

        countrySelectedFuture = new CompletableFuture<>();

        new Thread(() -> {
            try {
                JOptionPane.showMessageDialog(null, "Please click on the country from whom you want to select units. Keep in mind that you cannot move units from a country which has already been involved in this round.", "Select country", JOptionPane.INFORMATION_MESSAGE);
                String sourceCountry = countrySelectedFuture.get();
                countrySelectedFuture = new CompletableFuture<>();

                JOptionPane.showMessageDialog(null, "Please click on the country to whom you want to move your units. Keep in mind that you cannot move units to a country which has already been involved in this round.", "Select country", JOptionPane.INFORMATION_MESSAGE);
                String destinationCountry = countrySelectedFuture.get();
                countrySelectedFuture = new CompletableFuture<>();

                String units = JOptionPane.showInputDialog(null, "Enter unit amount (at least one unit has to remain in each country)", "Select units", JOptionPane.INFORMATION_MESSAGE);

                try{
                    gameManager.moveUnits(sourceCountry, destinationCountry, Integer.parseInt(units), false);
                    playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
                } catch(ExceptionCountryNotRecognized | ExceptionEmptyInput | ExceptionInvolvedCountrySelected | ExceptionCountryNotOwned | ExceptionTooManyUnits | ExceptionCountryIsNoNeighbour e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    class RiskMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "Show country infos" -> {

                    CountryTable countryTable = new CountryTable(new ArrayList<>(worldManager.getCountryMap().values()));
                    JFrame tableView = new JFrame("Country information");
                    tableView.setSize(600, 400);
                    JTable table = new JTable(countryTable);
                    JScrollPane scrollPane = new JScrollPane(table);
                    tableView.add(scrollPane);
                    tableView.setVisible(true);
                }
                case "Show my mission" -> JOptionPane.showMessageDialog(null, playerManager.getCurrentPlayerName() + " don't share your mission with anyone else! Your mission is: " + playerManager.getCurrentPlayerMission(), "Your mission", JOptionPane.INFORMATION_MESSAGE);
                case "Attack" -> {
                    try {
                        attack();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                case "Move units" -> {
                    try {
                        moveUnits();
                    } catch (IOException | InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                case "Distribute units" -> distributeUnits();
                case "Done, continue" -> {

                    switch (gameStep){
                        case(1) -> JOptionPane.showMessageDialog(null, "Please distribute all units before you continue.", "Error", JOptionPane.INFORMATION_MESSAGE);
                        case(2) -> gameStep++;
                        case(3) -> {
                            if(!playerManager.nextPlayersTurn()){
                                JOptionPane.showMessageDialog(null, "Congratulations!! You've won " + playerManager.getCurrentPlayerName(), "We have a winner!", JOptionPane.INFORMATION_MESSAGE);
                            }
                            gameStep = 1;
                            receiveUnits();
                        }
                    }
                    setActionButton();
                }
                case "Start new game" -> {

                    int decisionDelete = JOptionPane.showOptionDialog(null,"Previously saved data will be deleted. Do you want to continue?",
                            "Start new game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);

                    if (decisionDelete == JOptionPane.YES_OPTION){
                        try {
                            gameManager.newGame();
                            dispose();
                            StartView sView = new StartView(worldManager, playerManager, gameManager);
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
