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
    private final JLabel riskLabel;

    public RiskView(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager) throws IOException {

        this.worldManager = worldManager;
        this.playerManager = playerManager;
        this.gameManager = gameManager;
        this.territorySelectedFuture = new CompletableFuture<>();
        this.playerPanelMap = new HashMap<>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);

        setLayout(new BorderLayout());

        RiskBoardPanel boardPanel = new RiskBoardPanel(worldManager, this);
        add(boardPanel, BorderLayout.CENTER);
        riskMenu = new RiskMenu(playerManager, new RiskMenuListener());
        add(riskMenu, BorderLayout.NORTH);


        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(Color.WHITE);
        riskLabel = new JLabel("");
        riskLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        southPanel.add(riskLabel);

        southPanel.setPreferredSize(new Dimension(200, 50));
        add(southPanel, BorderLayout.SOUTH);

        initializeRiskPlayerPanel();
        setVisible(true);

        checkPreviousGameState();
    }


    private void initializeRiskPlayerPanel(){

        RiskPlayerPanel leftPanel = new RiskPlayerPanel(playerManager, playerManager.getPlayerAmount());
        RiskPlayerPanel rightPanel = new RiskPlayerPanel(playerManager, playerManager.getPlayerAmount());

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
            riskLabel.setText("Error: Selected territory not found.");
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
            riskLabel.setText("Congratulations!! You've won " + winner + "Your mission was: " + playerManager.getPlayerMission(winner));
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
        riskLabel.setText("It's your turn " + playerManager.getCurrentPlayerName() + ". You receive " + receivedUnits + " units. Click 'Distribute units' in the menu to distribute your received units to your territories.");
    }

    private void distributeUnits() {
        riskLabel.setText("You received " + gameManager.getReceivedUnits() + ". Please click on the territory on which you want to distribute your units.");
        territorySelectedFuture = new CompletableFuture<>();

        new Thread(() -> {
            try {
                while(gameManager.getReceivedUnits() != 0){
                    String selectedTerritory = territorySelectedFuture.get();
                    territorySelectedFuture = new CompletableFuture<>();
                    String units = JOptionPane.showInputDialog(null, "Enter unit amount", "Select unit amount", JOptionPane.INFORMATION_MESSAGE);

                    try {
                        gameManager.distributeUnits(selectedTerritory, Integer.parseInt(units));
                        riskLabel.setText(Integer.parseInt(units) + " have been moved to " + selectedTerritory + ". You have " + gameManager.getReceivedUnits()  + " left.");
                        playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
                    } catch (NumberFormatException f) {
                        riskLabel.setText("Error: Invalid unit amount selected.");
                    } catch (ExceptionEmptyInput | ExceptionTerritorySelectedNotOwned | ExceptionTooManyUnits e) {
                        riskLabel.setText("Error: " + e.getMessage());
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            riskLabel.setText("All units have been distributed. Click on 'attack' to attack or 'done, continue' to continue.");
            checkForWinner();
            gameStep++;
            setActionButton();
        }).start();
    }
    private void attack() throws InterruptedException, ExecutionException{

        territorySelectedFuture = new CompletableFuture<>();

        new Thread(() -> {
            try {
                riskLabel.setText("Please click on the territory to attack from.");
                String attackingTerritory = territorySelectedFuture.get();
                territorySelectedFuture = new CompletableFuture<>();

                riskLabel.setText("Please click on the territory you want to attack.");
                String attackedTerritory = territorySelectedFuture.get();
                territorySelectedFuture = new CompletableFuture<>();

                String units = JOptionPane.showInputDialog(null, "Enter unit amount (select max. 3 and keep in mind that one unit has to remain in your territory)", "Select units", JOptionPane.INFORMATION_MESSAGE);

                try {
                    List<Integer> attackerDiceResult = gameManager.attack(attackingTerritory, attackedTerritory, Integer.parseInt(units));

                    displayDiceResult(attackerDiceResult, true);
                    riskLabel.setText(attackingTerritory + " has attacked " + attackedTerritory + ". " + playerManager.getCurrentPlayerName() + " you've rolled " + attackerDiceResult);

                    defend(attackingTerritory, attackedTerritory, Integer.parseInt(units), attackerDiceResult);
                } catch (NumberFormatException f) {
                    riskLabel.setText("Error: Invalid unit amount selected.");
                } catch(ExceptionEmptyInput | ExceptionTerritorySelectedNotOwned |
                        ExceptionOwnTerritoryAttacked | ExceptionTerritoryIsNoNeighbour | ExceptionTooLessUnits | ExceptionTooManyUnits | IOException e){
                    riskLabel.setText("Error: " + e.getMessage());
                    attack();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void defend(String attackingTerritory, String attackedTerritory, int unitsFromAttacker, List<Integer> attackerDiceResult) throws IOException, InterruptedException {

        String defenderName = worldManager.getTerritoryOwner(attackedTerritory);
        riskLabel.setText("Your territory has been attacked " + defenderName + "! You have to defend it!");

        List<Integer> defenderDiceResult = gameManager.defend(attackedTerritory, attackingTerritory, attackerDiceResult, unitsFromAttacker);
        displayDiceResult(defenderDiceResult, false);
        riskLabel.setText(defenderName + " rolled " + defenderDiceResult + " and " + playerManager.getCurrentPlayerName() + " rolled " + attackerDiceResult + ". ");

        playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
        playerPanelMap.get(defenderName).updateList(defenderName);

        if (!worldManager.getTerritoryOwner(attackedTerritory).equals(playerManager.getCurrentPlayerName())) {

            riskLabel.setText(defenderName + " was able to defend " + attackedTerritory + ". " + worldManager.getUnitAmountOfTerritory(attackedTerritory) + " units remain in " + attackedTerritory + " and "
                    + worldManager.getUnitAmountOfTerritory(attackingTerritory) + " units remain in " + attackingTerritory);
            checkForWinner();
            return;
        }


        if(worldManager.getUnitAmountOfTerritory(attackingTerritory) > 1){ //if more units can be shifted to the conquered country

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
                    riskLabel.setText("Error: " + e.getMessage());
                }
            }
        }

        riskLabel.setText(worldManager.getUnitAmountOfTerritory(attackedTerritory) + " units remain in " + attackedTerritory + " and " + worldManager.getUnitAmountOfTerritory(attackingTerritory) + " units remain in " + attackingTerritory);

        if(checkForWinner()){
            return;
        }

        if (playerManager.isPlayerDefeated(defenderName)){
            riskLabel.setText(defenderName + " your last territory has been conquered, the game has to continue without you. ");
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
                riskLabel.setText("Please click on the source territory. Keep in mind that you cannot move units from a territory which has already been involved in this round.");
                String sourceTerritory = territorySelectedFuture.get();
                territorySelectedFuture = new CompletableFuture<>();

                riskLabel.setText("Please click on the destination territory. Keep in mind that you cannot move units to a territory which has already been involved in this round.");
                String destinationTerritory = territorySelectedFuture.get();
                territorySelectedFuture = new CompletableFuture<>();

                String units = JOptionPane.showInputDialog(null, "Enter unit amount (at least one unit has to remain in each territory)", "Select units", JOptionPane.INFORMATION_MESSAGE);

                try{
                    gameManager.moveUnits(sourceTerritory, destinationTerritory, Integer.parseInt(units), false);
                    playerPanelMap.get(playerManager.getCurrentPlayerName()).updateList(playerManager.getCurrentPlayerName());
                    riskLabel.setText(Integer.parseInt(units) + " have been moved from " + sourceTerritory + " to " + destinationTerritory + ". You can continue to move units or finish your turn by clicking 'done, continue'.");
                    checkForWinner();
                } catch(ExceptionEmptyInput | ExceptionInvolvedTerritorySelected |
                        ExceptionTerritorySelectedNotOwned | ExceptionTooManyUnits |
                        ExceptionTerritoryIsNoNeighbour e) {
                    riskLabel.setText("Error: " +  e.getMessage());
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
                        case(1) -> riskLabel.setText("Error: Please distribute all units before you continue.");
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
                            riskLabel.setText("Game saved successfully!");
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }
}
