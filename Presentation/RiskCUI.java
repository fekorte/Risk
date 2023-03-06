package Presentation;

import Business.IPlayerManager;
import Business.PlayerManager;
import Business.Round;
import Business.RoundManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RiskCUI {

    private final IPlayerManager playerManager;
    private final RoundManager roundManager;

    String currentPlayerName;

    private final BufferedReader in;

    boolean gameStarted;
    boolean gameSetUp;

    boolean doneWithStep;


    public RiskCUI() throws IOException {

        playerManager = new PlayerManager();
        roundManager = new Round();
        gameStarted = false;
        gameSetUp = false;
        doneWithStep = false;
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private void showMenu() {

        if(!gameStarted && !gameSetUp){
            System.out.print("Commands: \n  Start game:  's'");
        }

        if(gameStarted && !gameSetUp){
            System.out.print("Commands: \n  Add player:  'p'");
            System.out.print("         \n  Remove player:  'r'");
            System.out.print("         \n  Show all players:  'a'");
            System.out.print("         \n  Start game:  's'");
        }

        if(gameStarted && gameSetUp){
            System.out.print("Commands: \n  Show game overview:  'o'");
            System.out.print("         \n  Save game:  's'");
            System.out.print("         \n  Show all players:  'a'");
        }

        System.out.print("         \n  ---------------------");
        System.out.println("         \n  Quit:        'q'");
        System.out.print("> ");
        System.out.flush();
    }

    private void processInput(String line) throws IOException {

        if("q".equals(line)){
            gameStarted = false;
            gameSetUp = false;
            roundManager.endGame();
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
            case "a" -> playerManager.getPlayersInfo();
            case "p" -> { //add player

                System.out.println("Name > ");
                String playerName = readInput();
                System.out.println("Color > ");
                String playerColor = readInput();
                playerManager.addPlayer(playerName, playerColor);
            }
            case "r" -> { //remove player

                System.out.println("Name > ");
                String playerToRemove = readInput();
                playerManager.removePlayer(playerToRemove);
            }
            case "s" -> { //start game after players were selected

                gameSetUp = true;
                roundManager.startFirstRound();
                riskRound();
            }
        }
    }

    private void riskRound() throws IOException {

        System.out.println("New round! It's your turn " + currentPlayerName);



        //receiveUnits
        int receivedUnits = roundManager.receiveUnits();
        System.out.println("You receive " + receivedUnits + " units");



        //distribute units
        while(!doneWithStep){
            System.out.println("Please select what you want to do: ");
            gameOverviewMenu(false, false);

            String selectedAction = readInput();
            processGameInput(selectedAction, false, false);
        }
        doneWithStep = false;
        distributeUnits(receivedUnits);



        //attack and defend
        System.out.println("All your units have been distributed. Now it is time to attack.");
        while(!doneWithStep){
            System.out.println("Please select what you want to do: ");
            gameOverviewMenu(true, false);

            String selectedAction = readInput();
            processGameInput(selectedAction, true, false);
        }
        doneWithStep = false;



        //moveUnits
        System.out.println("Now move your units to neighbouring countries which belong to you.");
        while(!doneWithStep){
            System.out.println("Please select what you want to do: ");
            gameOverviewMenu(false, true);

            String selectedAction = readInput();
            processGameInput(selectedAction, false, true);
        }
        doneWithStep = false;




        System.out.println("Do you want to save the game? \n Y/N > ");
        if (readInput().equals("Y")) {
            roundManager.saveGame();
        }

        if(!roundManager.isMissionSolved()){
            currentPlayerName = roundManager.nextPlayersTurn();
            riskRound();
        } else {
            System.out.println("Congratulations!! You've won " + currentPlayerName);
            System.out.println(roundManager.getInfoAll());
            gameStarted = false;
            gameSetUp = false;
        }
    }

    private void gameOverviewMenu(boolean attack, boolean moveUnits){

        System.out.print("Commands: \n  Show your country infos:  'a'");
        System.out.print("         \n  Show all country infos:  'b'");
        System.out.print("         \n  Get info about neighbouring countries:  'c'");
        System.out.print("         \n  Done, continue with next step:  'd'");
        if(attack){
            System.out.print("         \n  Attack:  'e'");
        } else if(moveUnits){
            System.out.print("         \n  Move units:  'e'");
        } else {
            System.out.print("         \n  Distribute units:  'e'");
        }
    }

    private void processGameInput(String line, boolean attack, boolean moveUnits) throws IOException {

        switch(line) {
            case "a" -> //show players' country infos
                    System.out.println(roundManager.getInfoPlayer(currentPlayerName));

            case "b" -> //show all country infos
                    System.out.println(roundManager.getInfoAll());

            case "c" -> { //get info about neighbouring countries
                System.out.println("Country > ");
                String selectedCountry = readInput();
                System.out.println(roundManager.getNeighbourInfo(selectedCountry));
            }

            case "d" -> //done, continue with next step
                    doneWithStep = true;
        }

        if("e".equals(line) && attack){
            attack();
        } else if("e".equals(line) && moveUnits){
            moveUnits();
        } else {
            doneWithStep = true;
        }
    }

    private void distributeUnits(int receivedUnits) throws IOException {
        System.out.println("Now you have to distribute your units. Where do you want to place them? ");

        while(receivedUnits != 0){
            System.out.println("This is the current unit contribution: " + roundManager.getInfoPlayer(currentPlayerName));

            System.out.println("Country > ");
            String selectedCountry = readInput();
            System.out.println("Units > ");
            int units = Integer.parseInt(readInput());

            if(roundManager.distributeUnits(selectedCountry, units)){
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
        String diceResult = roundManager.attack(attackingCountry, attackedCountry, units);
        System.out.println(diceResult);

        defend(attackingCountry, attackedCountry, units);
    }

    private void defend(String attackingCountry, String attackedCountry, int unitsFromAttacker) throws IOException {
        System.out.println("Your country has been attacked " + roundManager.getCountryOwner(attackedCountry) + "! You have to defend it!");
        System.out.println("Units (select max. 2)  > ");
        int units = Integer.parseInt(readInput());

        System.out.println(roundManager.defend(attackedCountry, attackingCountry, units));

        if(roundManager.getCountryOwner(attackedCountry).equals(currentPlayerName)){
            System.out.println(currentPlayerName + " do you want to move additional units to the conquered country? Y/N > ");
            if (readInput().equals("Y")) {
                System.out.println("Please note that at least one unit has to remain in " + attackingCountry);
                System.out.println("Units > ");
                int unitsToMove = Integer.parseInt(readInput());
                roundManager.moveUnits(attackingCountry, attackedCountry, unitsToMove + unitsFromAttacker);
            } else {
                roundManager.moveUnits(attackingCountry, attackedCountry, unitsFromAttacker);
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

        roundManager.moveUnits(sourceCountry, destinationCountry, units);
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
