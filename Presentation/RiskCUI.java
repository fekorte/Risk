package Presentation;

import Business.*;
import Common.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RiskCUI {

    private final BufferedReader in;
    private final IPlayerManager playerManager;

    private final WorldManager worldManager;
    private final GameManager gameManager;

    Player currentPlayer;

    boolean gameStarted;
    boolean gameSetUp;

    boolean doneWithStep;


    public RiskCUI() throws IOException {

        in = new BufferedReader(new InputStreamReader(System.in));
        playerManager = new PlayerManager();
        worldManager = new World();
        gameManager = new Game();
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
            playerManager.clearPlayers();
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

                if(playerManager.readyToStartGame()){
                    gameSetUp = true;
                    currentPlayer = gameManager.startFirstRound();
                    riskTurn();
                }
            }
            case "c" -> //show available colors
                    System.out.println(playerManager.getAllowedColors());
        }
    }

    private void riskTurn() throws IOException {

        System.out.println("New round! It's your turn " + currentPlayer);

        //receiveUnits
        int receivedUnits = gameManager.receiveUnits();
        System.out.println("You receive " + receivedUnits + " units");


        //distribute units
        System.out.println("You can distribute your received units to your countries. You can inform yourself in this menu to plan better.");
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
                    System.out.println(worldManager.getAllCountriesFromPlayer(currentPlayer.getPlayerName()));

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

        System.out.println("Now you have to distribute your units. Where do you want to place them? ");

        while(receivedUnits != 0){
            System.out.println("This is the current unit contribution: " + worldManager.getAllCountriesFromPlayer(currentPlayer.getPlayerName()));

            System.out.println("Country > ");
            String selectedCountry = readInput();
            System.out.println("Units > ");
            int units = Integer.parseInt(readInput());

            if(gameManager.distributeUnits(selectedCountry, units)){
                receivedUnits =- units;
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
        System.out.println(gameManager.attack(attackingCountry, attackedCountry, units));

        defend(attackingCountry, attackedCountry, units);
    }

    private void defend(String attackingCountry, String attackedCountry, int unitsFromAttacker) throws IOException {

        System.out.println("Your country has been attacked " + worldManager.getCountryOwner(attackedCountry) + "! You have to defend it!");
        System.out.println("Units (select max. 2)  > ");
        int units = Integer.parseInt(readInput());

        System.out.println(gameManager.defend(attackedCountry, attackingCountry, units));

        if(worldManager.getCountryOwner(attackedCountry).equals(currentPlayer.getPlayerName())){
            System.out.println(currentPlayer + " do you want to move additional units to the conquered country? Y/N > ");
            if (readInput().equals("Y")) {
                System.out.println("Please note that at least one unit has to remain in " + attackingCountry);
                System.out.println("Units > ");
                int unitsToMove = Integer.parseInt(readInput());
                gameManager.moveUnits(attackingCountry, attackedCountry, unitsToMove + unitsFromAttacker);
            } else {
                gameManager.moveUnits(attackingCountry, attackedCountry, unitsFromAttacker);
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

        gameManager.moveUnits(sourceCountry, destinationCountry, units);
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
