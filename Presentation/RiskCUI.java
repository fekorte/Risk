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
    boolean gameStarted;
    boolean riskVersionSelected;
    boolean standardRisk;
    boolean gameSetUp;
    boolean doneWithStep;
    int gameStep;


    public RiskCUI(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager) throws IOException {

        in = new BufferedReader(new InputStreamReader(System.in));
        this.worldManager = worldManager;
        this.playerManager = playerManager;
        this.gameManager = gameManager;

        doneWithStep = false;
        if(playerManager.continuePreviousGame()){
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
                } catch (ExceptionPlayerAlreadyExists | ExceptionTooManyPlayer | ExceptionColorAlreadyExists e){
                    System.out.println(e.getMessage());
                }
            }
            case "b" -> { //remove player

                System.out.println("Name > ");
                String playerToRemove = readInput();
                try{
                    playerManager.removePlayer(playerToRemove);
                    System.out.println("Player " + playerToRemove + " has been removed successfully.");
                } catch(ExceptionObjectDoesntExist e){
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

    private void riskTurn() throws IOException {

        gameStep = 1;
        stepOne();
        stepTwo();
        stepThree();
    }


    //receive and distribute units
    private void stepOne() throws IOException {

        System.out.println("New round! It's your turn " + playerManager.getCurrentPlayerName());

        //receiveUnits
        int receivedUnits = 0;
        try{
            receivedUnits = gameManager.receiveUnits();
            System.out.println("You receive " + receivedUnits + " units.");
        } catch(ExceptionObjectDoesntExist e){
            e.printStackTrace();
        }

        //distribute units
        System.out.println("You can distribute your received units to your countries. You can inform yourself in this menu to plan better how to distribute your units.");
        playerChoice(false, false);
        distributeUnits(receivedUnits);
    }

    //attack and defend
    private void stepTwo() throws IOException {

        System.out.println("All your units have been distributed. Now it is time to attack.");
        playerChoice(true, false);
    }
    //moveUnits
    private void stepThree() throws IOException {

        System.out.println("Move your units to neighbouring countries which belong to you.");
        playerChoice(false, true);

        if(playerManager.nextPlayersTurn()){
            riskTurn();
        } else {
            System.out.println("Congratulations!! You've won " + playerManager.getCurrentPlayerName());
            System.out.println(worldManager.getAllCountryInfos());
            gameManager.quitGame();
            gameStarted = false;
            gameSetUp = false;
        }
    }

    private void playerChoice(boolean attack, boolean moveUnits) throws IOException {

        while(!doneWithStep){
            System.out.println("Please select what you want to do: ");
            gameOverviewMenu(attack, moveUnits);
            String selectedAction = readInput();
            processGameInput(selectedAction, attack, moveUnits);
        }
        doneWithStep = false;
    }

    private void gameOverviewMenu(boolean attack, boolean moveUnits){

        System.out.print("Current player: " + playerManager.getCurrentPlayerName() + ", Round: " + playerManager.getRound() + "\n");
        System.out.print("Commands: \n  Show all players:  'a'");
        System.out.print("         \n  Show all country infos:  'b'");
        System.out.print("         \n  Show your country infos:  'c'");
        System.out.print("         \n  Get info about neighbouring countries:  'd'");
        System.out.print("         \n  Show my mission:  'e'");
        System.out.println("         \n  Done, continue with next step:  'f'");
        if(attack){
            System.out.println("         \n  Attack:  'g'");
        } else if(moveUnits){
            System.out.println("         \n  Move units:  'g'");
        }
        System.out.print("         \n  ---------------------");
        System.out.print("         \n  Save game:  's'");
        System.out.print("         \n  Start new game:        'n'");
        System.out.println("         \n  Quit:        'q'");
        System.out.print("> ");
        System.out.flush();
    }

    private void processGameInput(String line, boolean attack, boolean moveUnits) throws IOException {

        switch(line) {
            case "a" -> System.out.println(playerManager.getPlayersInfo());

            case "b" -> //show all country infos
                    System.out.println(worldManager.getAllCountryInfos());

            case "c" -> //show players' country infos
                    System.out.println(playerManager.getAllCountriesInfoPlayer());

            case "d" -> { //get info about neighbouring countries
                System.out.println("Country > ");
                String selectedCountry = readInput();
                System.out.println(worldManager.getCountryNeighbours(selectedCountry));
            }

            case "e" -> System.out.println(playerManager.getCurrentPlayerMission());
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
        if("g".equals(line) && attack){
            attack();
        } else if("g".equals(line) && moveUnits) {
            moveUnits();
        }
    }

    private void distributeUnits(int receivedUnits) throws IOException {

        System.out.println("Now you have to distribute your units. " + "You received " + receivedUnits + ". Where do you want to place them? ");

        while (receivedUnits != 0) {
            System.out.println("This is the current unit contribution: " + playerManager.getAllCountriesInfoPlayer());

            System.out.println("Country > ");
            String selectedCountry = readInput();
            System.out.println("Units > ");
            int selectedUnits = Integer.parseInt(readInput());

            try {
                gameManager.distributeUnits(selectedCountry, selectedUnits, receivedUnits);
                receivedUnits -= selectedUnits;
                System.out.println(selectedUnits + " have been moved to " + selectedCountry + ". You have " + receivedUnits + " left.");
            } catch (ExceptionCountryNotRecognized | ExceptionCountryNotOwned | ExceptionTooManyUnits e) {
                System.out.println(e.getMessage());
            }
        }
    }
    private void attack() throws IOException {

        System.out.println("Country to attack from > ");
        String attackingCountry = readInput();
        System.out.println("Country to attack > ");
        String attackedCountry = readInput();
        System.out.println("Units (select max. 3, keep in mind that one unit has to remain in your country)  > ");
        int units = Integer.parseInt(readInput());
        try{
            List<Integer> attackerDiceResult = gameManager.attack(attackingCountry, attackedCountry, units);
            System.out.println(attackingCountry + " has attacked " + attackedCountry + ". " + playerManager.getCurrentPlayerName() + " you've rolled " + attackerDiceResult);
            defend(attackingCountry, attackedCountry, units, attackerDiceResult);
        } catch(ExceptionCountryNotRecognized | ExceptionCountryNotOwned | ExceptionCountryIsNoNeighbour | ExceptionTooLessUnits | ExceptionTooManyUnits e){
            System.out.println(e.getMessage());
        }
    }

    private void defend(String attackingCountry, String attackedCountry, int unitsFromAttacker, List<Integer> attackerDiceResult) throws IOException {

        String defenderName = worldManager.getCountryOwner(attackedCountry);

        System.out.println("Your country has been attacked " + defenderName + "! You have to defend it!");
        List<Integer> defenderDiceResult = gameManager.defend(attackedCountry, attackingCountry, attackerDiceResult, unitsFromAttacker);

        System.out.println(defenderName + " rolled " + defenderDiceResult + " and " + playerManager.getCurrentPlayerName() + " rolled " + attackerDiceResult + ". ");

        if(!worldManager.getCountryOwner(attackedCountry).equals(playerManager.getCurrentPlayerName())) {
            System.out.println(defenderName + " was able to defend " + attackedCountry + ". " + worldManager.getUnitAmountOfCountry(attackedCountry) + " units remain in " + attackedCountry + " and "
                    + worldManager.getUnitAmountOfCountry(attackingCountry) + " units remain in " + attackingCountry + "\n");
            return;
        }

        System.out.println(playerManager.getCurrentPlayerName() + " was able to conquer " + attackedCountry + ". " + attackingCountry + " unit amount: " +  worldManager.getUnitAmountOfCountry(attackingCountry) +
                ". Current unit amount in " + attackedCountry + ": " + worldManager.getUnitAmountOfCountry(attackedCountry) + ".");
        System.out.println(playerManager.getCurrentPlayerName() + " do you want to move additional units to the conquered country? Y/N > ");

        if (readInput().equals("Y")) {
            System.out.println("Please note that at least one unit has to remain in " + attackingCountry);

            System.out.println("Units > ");
            int unitsToMove = Integer.parseInt(readInput());
            try{
                gameManager.moveUnits(attackingCountry, attackedCountry, unitsToMove, true);
            } catch(ExceptionCountryNotRecognized | ExceptionInvolvedCountrySelected | ExceptionCountryNotOwned | ExceptionTooManyUnits | ExceptionCountryIsNoNeighbour e){
                System.out.println(e.getMessage());
            }
        }

        System.out.println(worldManager.getUnitAmountOfCountry(attackedCountry) + " units remain in " + attackedCountry + " and "
            + worldManager.getUnitAmountOfCountry(attackingCountry) + " units remain in " + attackingCountry + "\n");

        if(playerManager.playerDefeated(defenderName)){
        System.out.println(defenderName + " your last country has been conquered, the game has to continue without you.");
        try{
            playerManager.removePlayer(defenderName);
        } catch (ExceptionObjectDoesntExist e){
            e.printStackTrace();
        }

        if(playerManager.getPlayerNumber() == 1){
            System.out.println(playerManager.getCurrentPlayerName() + " congratulation, you've won!");
            gameStarted = false;
            gameSetUp = false;
            gameManager.quitGame();
        }
    }
    }

    private void moveUnits() throws IOException {

        System.out.println("Keep in mind that you cannot move units from a country which has been involved in this round.");
        System.out.println("Country to take units from > ");
        String sourceCountry = readInput();
        System.out.println("Country to move units to > ");
        String destinationCountry = readInput();
        System.out.println("Amount of units > ");
        int units = Integer.parseInt(readInput());

        try{
            gameManager.moveUnits(sourceCountry, destinationCountry, units, false);
        } catch(ExceptionCountryNotRecognized | ExceptionInvolvedCountrySelected | ExceptionCountryNotOwned | ExceptionTooManyUnits | ExceptionCountryIsNoNeighbour e) {
            System.out.println(e.getMessage());
        }
    }

    private String readInput() throws IOException {

        return in.readLine();
    }


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

        IPersistence persistence = new FilePersistence();

        IWorldManager worldManager = new WorldManager(persistence);
        IPlayerManager playerManager = new PlayerManager(worldManager, persistence);
        IGameManager gameManager = new GameManager(playerManager, worldManager, persistence);

        RiskCUI cui;
        try {
            cui = new RiskCUI(worldManager, playerManager, gameManager);
            cui.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
