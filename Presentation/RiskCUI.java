package Presentation;

import Business.*;
import Common.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RiskCUI {

    private final BufferedReader in;
    private final IPlayerManager playerManager;

    private final IWorldManager worldManager;
    private final GameManager gameManager;

    Player currentPlayer;
    boolean gameStarted;
    boolean gameSetUp;

    boolean doneWithStep;


    public RiskCUI() throws IOException {

        in = new BufferedReader(new InputStreamReader(System.in));
        worldManager = new World();
        playerManager = new PlayerManager(worldManager);
        gameManager = new Game(playerManager, worldManager);
        gameStarted = false;
        gameSetUp = false;
        doneWithStep = false;
    }

    private void showMenu() {

        if(!gameStarted && !gameSetUp){
            System.out.print("Commands: \n  Start game:  's'");
        }

        if(gameStarted && !gameSetUp){
            System.out.print("Commands: \n  Add player:  'p'");
            System.out.print("         \n  Remove player:  'r'");
            System.out.print("         \n  Show all players:  'a'");
            System.out.print("         \n  Show available colors: 'c'");
            System.out.print("         \n  Start game:  's'");
        }

        if(gameStarted && gameSetUp){
            System.out.print("Commands: \n  Show game overview:  'o'");
            System.out.print("         \n  Save game:  's'");
            System.out.print("         \n  Show all players:  'a'");
        }

        System.out.print("         \n  ---------------------");
        if(gameStarted){
            System.out.print("         \n  New game:        'n'");
        }
        System.out.println("         \n  Quit:        'q'");
        System.out.print("> ");
        System.out.flush();
    }

    private void processInput(String line) throws IOException {

        if("n".equals(line) && gameStarted){
            gameStarted = false;
            gameSetUp = false;
            gameManager.quitGame();
            return;
        }

        if(!gameStarted && !gameSetUp){ //process input for start game in the very beginning
            if ("s".equals(line)) {
                gameStarted = true;
                return;
            }
        }

        if(gameStarted && !gameSetUp){ //process input for game set up (select players)
            processGameSetUpInput(line);
        }
    }

    private void processGameSetUpInput(String line) throws IOException{

        switch (line) {
            case "a" -> System.out.println(playerManager.getPlayersInfo());
            case "p" -> { //add player

                System.out.println("Name > ");
                String playerName = readInput();
                System.out.println("Possible colors are: " + playerManager.getAllowedColors());
                System.out.println("Color > ");
                String playerColor = readInput();
                System.out.println("Player has been added. " + playerName + ", do not share your mission with anyone else. Your mission is: " +
                        playerManager.addPlayer(playerName, playerColor));
            }
            case "r" -> { //remove player

                System.out.println("Name > ");
                String playerToRemove = readInput();
                playerManager.removePlayer(playerToRemove);
            }
            case "s" -> { //start game after players were selected

                this.currentPlayer = gameManager.startFirstRound();
                if(currentPlayer != null){
                    gameSetUp = true;
                    riskTurn();
                }
            }
            case "c" -> //show available colors
                    System.out.println(playerManager.getAllowedColors());
        }
    }

    private void riskTurn() throws IOException {

        System.out.println("New round! It's your turn " + currentPlayer.getPlayerName());

        //receiveUnits
        int receivedUnits = gameManager.receiveUnits(currentPlayer.getPlayerName());
        System.out.println("You receive " + receivedUnits + " units.");


        //distribute units
        System.out.println("You can distribute your received units to your countries. You can inform yourself in this menu to plan better how to distribute your units.");
        playerChoice(false, false);
        distributeUnits(receivedUnits);


        //attack and defend
        System.out.println("All your units have been distributed. Now it is time to attack.");
        playerChoice(true, false);


        //moveUnits
        System.out.println("Move your units to neighbouring countries which belong to you.");
        playerChoice(false, true);


        System.out.println("Do you want to save the game? \n Y/N > ");
        if (readInput().equals("Y")) {
            gameManager.saveGame();
        }


        if(currentPlayer.getPlayerMission().isMissionCompleted(currentPlayer.getPlayerName())){
            currentPlayer = playerManager.nextPlayersTurn(currentPlayer.getPlayerName());
            riskTurn();
        } else {
            System.out.println("Congratulations!! You've won " + currentPlayer);
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

        System.out.print("Commands: \n  Show your country infos:  'a'");
        System.out.print("         \n  Show all country infos:  'b'");
        System.out.print("         \n  Get info about neighbouring countries:  'c'");
        System.out.println("         \n  Done, continue with next step:  'd'");
        if(attack){
            System.out.println("         \n  Attack:  'e'");
        } else if(moveUnits){
            System.out.println("         \n  Move units:  'e'");
        }
        System.out.print("> ");
        System.out.flush();
    }

    private void processGameInput(String line, boolean attack, boolean moveUnits) throws IOException {

        switch(line) {
            case "a" -> //show players' country infos
                    System.out.println(gameManager.getAllCountriesInfoPlayer(currentPlayer.getPlayerName()));

            case "b" -> //show all country infos
                    System.out.println(worldManager.getAllCountryInfos());

            case "c" -> { //get info about neighbouring countries
                System.out.println("Country > ");
                String selectedCountry = readInput();
                System.out.println(worldManager.getCountryNeighbours(selectedCountry));
            }

            case "d" -> //done, continue with next step
                    doneWithStep = true;
        }

        if("e".equals(line) && attack){
            attack();
        } else if("e".equals(line) && moveUnits) {
            moveUnits();
        }
    }

    private void distributeUnits(int receivedUnits) throws IOException {

        System.out.println("Now you have to distribute your units. " + "You received " + receivedUnits + ". Where do you want to place them? ");

        while(receivedUnits != 0){
            System.out.println("This is the current unit contribution: " + gameManager.getAllCountriesInfoPlayer(currentPlayer.getPlayerName()));

            System.out.println("Country > ");
            String selectedCountry = readInput();
            System.out.println("Units > ");
            int units = Integer.parseInt(readInput());

            if(gameManager.distributeUnits(selectedCountry, units)){
                receivedUnits -= units;
                System.out.println(units + " have been moved to " + selectedCountry + ". You have " + receivedUnits + " left.");
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
        List<Integer> attackerDiceResult = gameManager.attack(attackingCountry, attackedCountry, units);
        System.out.println(attackingCountry + " has attacked " + attackedCountry + ". " + currentPlayer.getPlayerName() + " you've rolled " + attackerDiceResult);
        defend(attackingCountry, attackedCountry, units, attackerDiceResult);
    }

    private void defend(String attackingCountry, String attackedCountry, int unitsFromAttacker, List<Integer> attackerDiceResult) throws IOException {

        String defenderName = gameManager.getCountryOwner(attackedCountry);

        System.out.println("Your country has been attacked " + defenderName + "! You have to defend it!");
        List<Integer> defenderDiceResult = gameManager.defend(attackedCountry, attackingCountry, attackerDiceResult, unitsFromAttacker);

        System.out.println(defenderName + " rolled " + defenderDiceResult + " and " + currentPlayer.getPlayerName() + " rolled " + attackerDiceResult + ". ");

        if(!gameManager.getCountryOwner(attackedCountry).equals(currentPlayer.getPlayerName())){
            System.out.println(defenderName + " was able to defend " + attackedCountry + ". ");

        } else {
            System.out.println(currentPlayer.getPlayerName() + " was able to conquer " + attackedCountry + ". " + attackingCountry + " unit amount: " +  worldManager.getCountryMap().get(attackingCountry).getArmy().getUnits() +
            ". Current unit amount in " + attackedCountry + ": " + worldManager.getCountryMap().get(attackedCountry).getArmy().getUnits()+ ".");
            System.out.println(currentPlayer + " do you want to move additional units to the conquered country? Y/N > ");

            if (readInput().equals("Y")) {
                System.out.println("Please note that at least one unit has to remain in " + attackingCountry);

                System.out.println("Units > ");
                int unitsToMove = Integer.parseInt(readInput());
                gameManager.moveUnits(attackingCountry, attackedCountry, unitsToMove, true);
            }
        }
        System.out.println(worldManager.getCountryMap().get(attackedCountry).getArmy().getUnits() + " units remain in " + attackedCountry + " and "
                + worldManager.getCountryMap().get(attackingCountry).getArmy().getUnits() + " units remain in " + attackingCountry + "\n");

        if(gameManager.getAllCountriesInfoPlayer(defenderName).isEmpty()){
            System.out.println(defenderName + " your last country has been conquered, the game has to continue without you.");
            playerManager.removePlayer(defenderName);
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

        gameManager.moveUnits(sourceCountry, destinationCountry, units, false);
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

    public static void main(String[] args) {

        RiskCUI cui;
        try {
            cui = new RiskCUI();
            cui.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
