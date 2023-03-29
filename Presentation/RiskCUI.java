package Presentation;

import Business.*;
import Common.Exceptions.*;
import Persistence.FilePersistence;
import Persistence.IPersistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class RiskCUI {

    private final BufferedReader in;
    private final IPlayerManager playerManager;
    private final IWorldManager worldManager;
    private final IGameManager gameManager;
    private boolean gameStarted;
    private boolean riskVersionSelected;
    private boolean standardRisk;
    private boolean gameSetUp;
    private boolean doneWithStep;
    private int gameStep;

    public RiskCUI(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager) throws IOException {

        in = new BufferedReader(new InputStreamReader(System.in));
        this.worldManager = worldManager;
        this.playerManager = playerManager;
        this.gameManager = gameManager;

        doneWithStep = false;
        if(playerManager.getContinuePreviousGame()){
            gameStarted = true;
            riskVersionSelected = true;
            gameSetUp = true;
            gameStep = gameManager.getSavedGameStep();
            switch(gameStep){
                case(1) -> riskTurn();
                case (2) -> {
                    stepTwo();
                    stepThree();
                }
                case(3) -> stepThree();
            }
        } else {
            gameStarted = false;
            riskVersionSelected = false;
            gameSetUp = false;
            gameStep = 1;
        }
    }

    private void showMenu() {

        if(!gameStarted && !gameSetUp){
            System.out.print("Commands: \n  Start game:  's'");
        }

        if(gameStarted && !gameSetUp && !riskVersionSelected){
            System.out.print("Commands: \n  Play standard version:  'a'");
            System.out.print("         \n  Play mission risk:  'b'");
        }

        if(gameStarted && !gameSetUp && riskVersionSelected){
            System.out.print("Commands: \n  Add player:  'a'");
            System.out.print("         \n  Remove player:  'b'");
            System.out.print("         \n  Show all players:  'c'");
            System.out.print("         \n  Show available colors: 'd'");
            System.out.print("         \n  Start game:  's'");
        }
        System.out.print("         \n  ---------------------");
        System.out.println("         \n  Quit:        'q'");
        System.out.print("> ");
        System.out.flush();
    }

    private void processInput(String line) throws IOException {

        if(!gameStarted && !gameSetUp){ //process input for start game in the very beginning
            if ("s".equals(line)) {
                gameStarted = true;
                return;
            }
        }

        if(gameStarted && !gameSetUp && !riskVersionSelected){
            if ("a".equals(line)) {
                riskVersionSelected = true;
                standardRisk = true;
                return;
            }
            if ("b".equals(line)) {
                riskVersionSelected = true;
                standardRisk = false;
                return;
            }
        }

        if(gameStarted && !gameSetUp && riskVersionSelected){ //process input for game set up (select players)
            processGameSetUpInput(line);
        }
    }

    private void processGameSetUpInput(String line) throws IOException{

        switch (line) {
            case "a" -> { //add player

                System.out.println("Name > ");
                String playerName = readInput();
                System.out.println("Possible colors are: " + playerManager.getAllowedColors());
                System.out.println("Color > ");
                String playerColor = readInput();
                try{
                    playerManager.addPlayer(playerName, playerColor);
                    System.out.println( playerName + " has been added. ");
                } catch (ExceptionEmptyInput | ExceptionPlayerAlreadyExists | ExceptionTooManyPlayers | ExceptionColorAlreadyExists e){
                    System.out.println(e.getMessage());
                }
            }
            case "b" -> { //remove player

                System.out.println("Name > ");
                String playerToRemove = readInput();
                try{
                    playerManager.removePlayer(playerToRemove);
                    System.out.println("Player " + playerToRemove + " has been removed successfully.");
                } catch(ExceptionEmptyInput | ExceptionObjectDoesntExist e){
                    System.out.println(e.getMessage());
                }
            }

            case "c" -> System.out.println(playerManager.getPlayersInfo());

            case "d" -> //show available colors
                    System.out.println(playerManager.getAllowedColors());

            case "s" -> { //start game after players were selected
                try{
                    gameManager.startFirstRound(standardRisk);
                    gameSetUp = true;
                    riskTurn();
                } catch (ExceptionNotEnoughPlayer e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void showGameOverviewMenu(){

        System.out.print("Current player: " + playerManager.getCurrentPlayerName() + ", Round: " + playerManager.getRound() + "\n");
        System.out.print("Commands: \n  Show all players:  'a'");
        System.out.print("         \n  Show all world infos:  'b'");
        System.out.print("         \n  Show your territory infos:  'c'");
        System.out.print("         \n  Get info about neighbouring territories:  'd'");
        System.out.print("         \n  Show my mission:  'e'");
        System.out.println("         \n  Done, continue with next step:  'f'");
        if(gameStep == 2){
            System.out.println("         \n  Attack:  'g'");
        } else if(gameStep == 3){
            System.out.println("         \n  Move units:  'g'");
        }
        System.out.print("         \n  ---------------------");
        System.out.print("         \n  Save game:  's'");
        System.out.print("         \n  Start new game:        'n'");
        System.out.println("         \n  Quit:        'q'");
        System.out.print("> ");
        System.out.flush();
    }

    private void processGameInput(String line) throws IOException {

        switch(line) {
            case "a" -> System.out.println(playerManager.getPlayersInfo());

            case "b" -> //show all world infos
                    System.out.println(worldManager.getWorldInfos());

            case "c" -> //show players' territory infos
                    System.out.println(playerManager.getAllTerritoryInfosPlayer(playerManager.getCurrentPlayerName()));

            case "d" -> { //get info about neighbouring territories
                System.out.println("Country > ");
                String selectedCountry = readInput();
                System.out.println(worldManager.getTerritoryNeighbours(selectedCountry));
            }

            case "e" -> System.out.println(playerManager.getPlayerMission(playerManager.getCurrentPlayerName()));
            case "f" -> { //done, continue with next step
                doneWithStep = true;
                gameStep++;
            }
            case "n" -> { //start new game
                gameStarted = false;
                gameSetUp = false;
                System.out.println("Previously saved data will be deleted. Do you want to continue? Y/N > ");
                if (readInput().equals("Y")) {
                    gameManager.newGame();
                }
            }
            case "s" -> {
                if (gameManager.saveGame(gameStep)) {
                    System.out.println("Game saved successfully!");
                }
            }
            case "q" -> System.exit(0);
        }
        if("g".equals(line) && gameStep == 2){
            attack();
        } else if("g".equals(line) && gameStep == 3) {
            moveUnits();
        }
    }

    private void riskTurn() throws IOException {

        gameStep = 1;
        stepOne();
        stepTwo();
        stepThree();
    }

    private void playerChoice() throws IOException {

        while(!doneWithStep){
            System.out.println("Please select what you want to do: ");
            showGameOverviewMenu();
            String selectedAction = readInput();
            processGameInput(selectedAction);
        }
        doneWithStep = false;
    }


    //receive and distribute units
    private void stepOne() throws IOException {

        System.out.println("New round! It's your turn " + playerManager.getCurrentPlayerName());

        //receiveUnits
        int receivedUnits;
        try{
            gameManager.receiveUnits();
            receivedUnits = gameManager.getReceivedUnits();
            System.out.println("You receive " + receivedUnits + " units.");
        } catch(ExceptionObjectDoesntExist e){
            e.printStackTrace();
        }

        //distribute units
        System.out.println("You can distribute your received units to your territories. You can inform yourself in this menu to plan better how to distribute your units.");
        playerChoice();
        distributeUnits();
    }

    private void distributeUnits() throws IOException {

        System.out.println("Now you have to distribute your units. You received " + gameManager.getReceivedUnits() + ". Where do you want to place them? ");

        while (gameManager.getReceivedUnits() != 0) {
            System.out.println("This is the current unit contribution: " + playerManager.getAllTerritoryInfosPlayer(playerManager.getCurrentPlayerName()));

            System.out.println("Territory > ");
            String selectedTerritory = readInput();
            System.out.println("Units > ");
            int selectedUnits = Integer.parseInt(readInput());

            try {
                gameManager.distributeUnits(selectedTerritory, selectedUnits);
                System.out.println(selectedUnits + " have been moved to " + selectedTerritory + ". You have " + gameManager.getReceivedUnits()  + " left.");
            } catch (ExceptionEmptyInput | ExceptionTerritoryNotRecognized | ExceptionTerritorySelectedNotOwned | ExceptionTooManyUnits e) {
                System.out.println(e.getMessage());
            }
        }
        checkForWinner();
    }

    //attack and defend
    private void stepTwo() throws IOException {

        System.out.println("All your units have been distributed. Now it is time to attack.");
        playerChoice();
    }

    private void attack() throws IOException {

        System.out.println("Territory to attack from > ");
        String attackingTerritory = readInput();
        System.out.println("Territory to attack > ");
        String attackedTerritory = readInput();
        System.out.println("Units (select max. 3, keep in mind that one unit has to remain in your territory)  > ");
        int units = Integer.parseInt(readInput());
        try{
            List<Integer> attackerDiceResult = gameManager.attack(attackingTerritory, attackedTerritory, units);
            System.out.println(attackingTerritory + " has attacked " + attackedTerritory + ". " + playerManager.getCurrentPlayerName() + " you've rolled " + attackerDiceResult);
            defend(attackingTerritory, attackedTerritory, units, attackerDiceResult);
        } catch(ExceptionTerritoryNotRecognized | ExceptionEmptyInput | ExceptionTerritorySelectedNotOwned |
                ExceptionOwnTerritoryAttacked | ExceptionTerritoryIsNoNeighbour | ExceptionTooLessUnits | ExceptionTooManyUnits e){
            System.out.println(e.getMessage());
        }
    }

    private void defend(String attackingTerritory, String attackedTerritory, int unitsFromAttacker, List<Integer> attackerDiceResult) throws IOException {

        String defenderName = worldManager.getTerritoryOwner(attackedTerritory);

        System.out.println("Your territory has been attacked " + defenderName + "! You have to defend it!");
        List<Integer> defenderDiceResult = gameManager.defend(attackedTerritory, attackingTerritory, attackerDiceResult, unitsFromAttacker);

        System.out.println(defenderName + " rolled " + defenderDiceResult + " and " + playerManager.getCurrentPlayerName() + " rolled " + attackerDiceResult + ". ");

        if(!worldManager.getTerritoryOwner(attackedTerritory).equals(playerManager.getCurrentPlayerName())) {
            System.out.println(defenderName + " was able to defend " + attackedTerritory + ". " + worldManager.getUnitAmountOfTerritory(attackedTerritory) + " units remain in " + attackedTerritory + " and "
                    + worldManager.getUnitAmountOfTerritory(attackingTerritory) + " units remain in " + attackingTerritory + "\n");
            return;
        }

        System.out.println(playerManager.getCurrentPlayerName() + " was able to conquer " + attackedTerritory + ". " + attackingTerritory + " unit amount: " +  worldManager.getUnitAmountOfTerritory(attackingTerritory) +
                ". Current unit amount in " + attackedTerritory + ": " + worldManager.getUnitAmountOfTerritory(attackedTerritory) + ".");
        System.out.println(playerManager.getCurrentPlayerName() + " do you want to move additional units to the conquered territory? Y/N > ");

        if (readInput().equals("Y")) {
            System.out.println("Please note that at least one unit has to remain in " + attackingTerritory);

            System.out.println("Units > ");
            int unitsToMove = Integer.parseInt(readInput());
            try{
                gameManager.moveUnits(attackingTerritory, attackedTerritory, unitsToMove, true);
            } catch(ExceptionTerritoryNotRecognized | ExceptionEmptyInput | ExceptionInvolvedTerritorySelected |
                    ExceptionTerritorySelectedNotOwned | ExceptionTooManyUnits |
                    ExceptionTerritoryIsNoNeighbour e){
                System.out.println(e.getMessage());
            }
        }

        System.out.println(worldManager.getUnitAmountOfTerritory(attackedTerritory) + " units remain in " + attackedTerritory + " and "
                + worldManager.getUnitAmountOfTerritory(attackingTerritory) + " units remain in " + attackingTerritory + "\n");


        if(checkForWinner()){
            return;
        }

        if(playerManager.isPlayerDefeated(defenderName)) {
            System.out.println(defenderName + " your last territory has been conquered, the game has to continue without you.");
            try {
                playerManager.removePlayer(defenderName);
            } catch (ExceptionEmptyInput | ExceptionObjectDoesntExist e) {
                e.printStackTrace();
            }
        }
    }

    //moveUnits
    private void stepThree() throws IOException {

        System.out.println("Move your units to neighbouring territories which belong to you.");
        playerChoice();


        if(!checkForWinner()){
            playerManager.nextPlayersTurn();
            riskTurn();
        }
    }
    private void moveUnits() throws IOException {

        System.out.println("Keep in mind that you cannot move units from a territory which has been involved in this round.");
        System.out.println("Territory to take units from > ");
        String sourceCountry = readInput();
        System.out.println("Territory to move units to > ");
        String destinationCountry = readInput();
        System.out.println("Amount of units > ");
        int units = Integer.parseInt(readInput());

        try{
            gameManager.moveUnits(sourceCountry, destinationCountry, units, false);
            checkForWinner();
        } catch(ExceptionEmptyInput | ExceptionTerritoryNotRecognized | ExceptionInvolvedTerritorySelected |
                ExceptionTerritorySelectedNotOwned | ExceptionTooManyUnits | ExceptionTerritoryIsNoNeighbour e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean checkForWinner() throws IOException {

        String winner = playerManager.isAnyMissionCompleted();
        if(winner != null){
            System.out.println("Congratulations!! You've won " + winner);
            System.out.println(worldManager.getWorldInfos());
            gameStarted = false;
            gameSetUp = false;
            gameManager.quitGame();
        }
        return winner != null;
    }

    private String readInput() throws IOException { return in.readLine(); }

    public void run() {

        String input = "";
        do {
            showMenu();

            try {
                input = readInput();
                processInput(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!input.equals("q"));
    }

    public static void main(String[] args) throws IOException {

        IWorldManager worldManager = new WorldManager();
        IPlayerManager playerManager = new PlayerManager(worldManager);
        IGameManager gameManager = new GameManager(playerManager, worldManager);

        RiskCUI cui;
        try {
            cui = new RiskCUI(worldManager, playerManager, gameManager);
            cui.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
