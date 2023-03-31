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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RiskView extends JFrame implements RiskBoardPanel.RiskBoardListener {

    private final IPlayerManager playerManager;
    private final IWorldManager worldManager;
    private final IGameManager gameManager;
    private CompletableFuture<String> territorySelectedFuture;
    private final Map<String, RiskPlayerPanel> playerPanelMap; //key is player name
    private final RiskMenu riskMenu;
    private int gameStep;

    public RiskView(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager) throws IOException {

        this.worldManager = worldManager;
        this.playerManager = playerManager;
        this.gameManager = gameManager;
        this.territorySelectedFuture = new CompletableFuture<>();
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
        for (int i = 0; i < playerManager.getPlayerAmount(); i++) {
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

        gameStep = (playerManager.getContinuePreviousGame()) ? gameManager.getSavedGameStep() : 1;
        setActionButton();

        if(gameStep == 1){
            receiveUnits();
        }
    }

    public void onTerritorySelected(String territoryName) {

        if(territoryName == null){
            JOptionPane.showMessageDialog(null, "Selected territory not found." , "Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
            this.territorySelectedFuture.complete(territoryName);
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

    private boolean checkForWinner(){

        String winner = playerManager.isAnyMissionCompleted();
        if(winner != null) {
            JOptionPane.showMessageDialog(null, "Congratulations!! You've won " + winner, "We have a winner!", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(null, "Your mission was: " + playerManager.getPlayerMission(winner), "Mission accomplished", JOptionPane.INFORMATION_MESSAGE);

            WorldTableView infoView = new WorldTableView(worldManager);
            dispose();
        }
        return winner != null;
    }

    private void receiveUnits(){

        int receivedUnits = 0;
        try{
            gameManager.receiveUnits();
            receivedUnits = gameManager.getReceivedUnits();
        } catch(ExceptionObjectDoesntExist e){
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "It's your turn " + playerManager.getCurrentPlayerName() + ". \n You receive " + receivedUnits + " units. \n Click 'Distribute units' in the menu to distribute your received units to your territories.", playerManager.getCurrentPlayerName() + "'s turn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void distributeUnits() {
        JOptionPane.showMessageDialog(null, "You received " + gameManager.getReceivedUnits() + ". \n Please click on the territory on which you want to distribute your units. ", "Distribute units", JOptionPane.INFORMATION_MESSAGE);
        territorySelectedFuture = new CompletableFuture<>();

        new Thread(() -> {
            try {
                while(gameManager.getReceivedUnits() != 0){
                    String selectedTerritory = territorySelectedFuture.get();
                    territorySelectedFuture = new CompletableFuture<>();
                    String units = JOptionPane.showInputDialog(null, "Enter unit amount", "Select unit amount", JOptionPane.INFORMATION_MESSAGE);

                    try {
                        gameManager.distributeUnits(selectedTerritory, Integer.parseInt(units));
                        JOptionPane.showMessageDialog(null,  Integer.parseInt(units) + " have been moved to " + selectedTerritory + ". You have " + gameManager.getReceivedUnits()  + " left.", "Distribute units", JOptionPane.INFORMATION_MESSAGE);
                        playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
                    } catch (NumberFormatException f) {
                        JOptionPane.showMessageDialog(null, "Invalid unit amount selected.", "Error", JOptionPane.INFORMATION_MESSAGE);
                    } catch (ExceptionEmptyInput | ExceptionTerritorySelectedNotOwned | ExceptionTooManyUnits e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "All units have been distributed. \nClick on 'attack' to attack or 'done, continue' to continue.", "Error", JOptionPane.INFORMATION_MESSAGE);
            checkForWinner();
            gameStep++;
            setActionButton();
        }).start();
    }
    private void attack() throws InterruptedException, ExecutionException{

        territorySelectedFuture = new CompletableFuture<>();

        new Thread(() -> {
            try {
                JOptionPane.showMessageDialog(null, "Please click on the territory to attack from.", "Select territory", JOptionPane.INFORMATION_MESSAGE);
                String attackingTerritory = territorySelectedFuture.get();
                territorySelectedFuture = new CompletableFuture<>();

                JOptionPane.showMessageDialog(null, "Please click on the territory you want to attack.", "Select territory", JOptionPane.INFORMATION_MESSAGE);
                String attackedTerritory = territorySelectedFuture.get();
                territorySelectedFuture = new CompletableFuture<>();

                String units = JOptionPane.showInputDialog(null, "Enter unit amount (select max. 3 and keep in mind that one unit has to remain in your territory)", "Select units", JOptionPane.INFORMATION_MESSAGE);

                try {
                    List<Integer> attackerDiceResult = gameManager.attack(attackingTerritory, attackedTerritory, Integer.parseInt(units));

                    displayDiceResult(attackerDiceResult, true);
                    JOptionPane.showMessageDialog(null, attackingTerritory + " has attacked " + attackedTerritory + ". " + playerManager.getCurrentPlayerName() + " you've rolled " + attackerDiceResult, "Dice result", JOptionPane.INFORMATION_MESSAGE);

                    defend(attackingTerritory, attackedTerritory, Integer.parseInt(units), attackerDiceResult);
                } catch (NumberFormatException f) {
                    JOptionPane.showMessageDialog(null, "Invalid unit amount selected.", "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch(ExceptionEmptyInput | ExceptionTerritorySelectedNotOwned |
                        ExceptionOwnTerritoryAttacked | ExceptionTerritoryIsNoNeighbour | ExceptionTooLessUnits | ExceptionTooManyUnits | IOException e){
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                    attack();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void defend(String attackingTerritory, String attackedTerritory, int unitsFromAttacker, List<Integer> attackerDiceResult) throws IOException, InterruptedException {

        String defenderName = worldManager.getTerritoryOwner(attackedTerritory);
        JOptionPane.showMessageDialog(null, "Your territory has been attacked " + defenderName + "! You have to defend it!", "Defend territory", JOptionPane.INFORMATION_MESSAGE);

        List<Integer> defenderDiceResult = gameManager.defend(attackedTerritory, attackingTerritory, attackerDiceResult, unitsFromAttacker);
        displayDiceResult(defenderDiceResult, false);
        JOptionPane.showMessageDialog(null, defenderName + " rolled " + defenderDiceResult + " and " + playerManager.getCurrentPlayerName() + " rolled " + attackerDiceResult + ". ", "Dice results", JOptionPane.INFORMATION_MESSAGE);


        playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
        playerPanelMap.get(defenderName).updateList(defenderName);

        if (!worldManager.getTerritoryOwner(attackedTerritory).equals(playerManager.getCurrentPlayerName())) {

            JOptionPane.showMessageDialog(null, defenderName + " was able to defend " + attackedTerritory + ".\n" + worldManager.getUnitAmountOfTerritory(attackedTerritory) + " units remain in " + attackedTerritory + " and "
                    + worldManager.getUnitAmountOfTerritory(attackingTerritory) + " units remain in " + attackingTerritory, attackedTerritory + " defended", JOptionPane.INFORMATION_MESSAGE);
            checkForWinner();
            return;
        }

        int moveUnitsDecision = JOptionPane.showOptionDialog(null,
                playerManager.getCurrentPlayerName() + " was able to conquer " + attackedTerritory + ".\n" + attackingTerritory + " unit amount: " + worldManager.getUnitAmountOfTerritory(attackingTerritory) +
                        ". Current unit amount in " + attackedTerritory + ": " + worldManager.getUnitAmountOfTerritory(attackedTerritory) + ". \n" + playerManager.getCurrentPlayerName() + " do you want to move additional units to " + attackedTerritory + "?",
                attackedTerritory + " conquered", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);


        if (moveUnitsDecision == JOptionPane.YES_OPTION){
            String units = JOptionPane.showInputDialog(null, "Please note that at least one unit has to remain in " + attackingTerritory, "Select units", JOptionPane.INFORMATION_MESSAGE);
            try {
                gameManager.moveUnits(attackingTerritory, attackedTerritory, Integer.parseInt(units), true);
                playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
            } catch (ExceptionEmptyInput | ExceptionInvolvedTerritorySelected |
                     ExceptionTerritorySelectedNotOwned |
                     ExceptionTooManyUnits | ExceptionTerritoryIsNoNeighbour e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        JOptionPane.showMessageDialog(null, worldManager.getUnitAmountOfTerritory(attackedTerritory) + " units remain in " + attackedTerritory + " and "
                + worldManager.getUnitAmountOfTerritory(attackingTerritory) + " units remain in " + attackingTerritory, "Result of fight", JOptionPane.INFORMATION_MESSAGE);

        if(checkForWinner()){
            return;
        }

        if (playerManager.isPlayerDefeated(defenderName)){
            JOptionPane.showMessageDialog(null, defenderName + " your last territory has been conquered, the game has to continue without you. ", defenderName + " lost", JOptionPane.INFORMATION_MESSAGE);
            try {
                playerManager.removePlayer(defenderName);
            } catch (ExceptionEmptyInput | ExceptionObjectDoesntExist e) {
                e.printStackTrace();
            }
        }
    }

    private void displayDiceResult(List<Integer> diceResult, boolean attack) throws InterruptedException {
        int index = 0;
        int numDices = diceResult.size();
        Frame diceRollFrame = new Frame("");
        DiceImageComponent diceImageComponent = new DiceImageComponent();

        while (numDices != 0) {
            diceRollFrame.add(diceImageComponent);

            Image diceImage = (attack) ? Toolkit.getDefaultToolkit().getImage("Images/r" + diceResult.get(index) + ".jpg") : Toolkit.getDefaultToolkit().getImage("Images/b" + diceResult.get(index) + ".jpg");
            diceImageComponent.setDiceImage(diceImage);
            diceRollFrame.setSize(diceImage.getWidth(null), diceImage.getHeight(null));
            diceRollFrame.setSize(200, 235);
            diceRollFrame.setLocationRelativeTo(null);
            diceRollFrame.setVisible(true);
            index++;
            numDices--;
            Thread.sleep(3200);
        }
        diceRollFrame.dispose();
    }

    private void moveUnits() throws IOException, InterruptedException, ExecutionException {

        territorySelectedFuture = new CompletableFuture<>();

        new Thread(() -> {
            try {
                JOptionPane.showMessageDialog(null, "Please click on the source territory. \nKeep in mind that you cannot move units from a territory which has already been involved in this round.", "Select territory", JOptionPane.INFORMATION_MESSAGE);
                String sourceTerritory = territorySelectedFuture.get();
                territorySelectedFuture = new CompletableFuture<>();

                JOptionPane.showMessageDialog(null, "Please click on the destination territory.\n Keep in mind that you cannot move units to a territory which has already been involved in this round.", "Select territory", JOptionPane.INFORMATION_MESSAGE);
                String destinationTerritory = territorySelectedFuture.get();
                territorySelectedFuture = new CompletableFuture<>();

                String units = JOptionPane.showInputDialog(null, "Enter unit amount (at least one unit has to remain in each territory)", "Select units", JOptionPane.INFORMATION_MESSAGE);

                try{
                    gameManager.moveUnits(sourceTerritory, destinationTerritory, Integer.parseInt(units), false);
                    playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
                    JOptionPane.showMessageDialog(null, Integer.parseInt(units) + " have been moved from " + sourceTerritory + " to " + destinationTerritory + ".\n You can continue to move units or finish your turn by clicking 'done, continue'.", "Select units", JOptionPane.INFORMATION_MESSAGE);
                    checkForWinner();
                } catch(ExceptionEmptyInput | ExceptionInvolvedTerritorySelected |
                        ExceptionTerritorySelectedNotOwned | ExceptionTooManyUnits |
                        ExceptionTerritoryIsNoNeighbour e) {
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
                case "Show world infos" -> { WorldTableView infoView = new WorldTableView(worldManager); }
                case "Show my mission" -> {
                    JOptionPane.showMessageDialog(null, playerManager.getCurrentPlayerName() + " don't share your mission with anyone else!", "Your mission", JOptionPane.INFORMATION_MESSAGE);
                    JOptionPane.showMessageDialog(null, "Your mission is: " + playerManager.getPlayerMission(playerManager.getCurrentPlayerName()), "Your mission", JOptionPane.INFORMATION_MESSAGE);
                }
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
                            if(checkForWinner()){
                                return;
                            }
                            playerManager.nextPlayersTurn();
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
