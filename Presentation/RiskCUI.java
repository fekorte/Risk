package Presentation;

import Business.RiskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RiskCUI {

    private RiskManager riskManager;
    private BufferedReader in;

    boolean gameStarted;
    boolean gameSetUp;

    public RiskCUI() throws IOException {

        gameStarted = false;
        gameSetUp = false;
        riskManager = new RiskManager();
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
            return;
        }

        if(!gameStarted && !gameSetUp){ //process input for start game in the very beginning
            if ("s".equals(line)) {
                gameStarted = true;
                return;
            }
        }

        if("a".equals(line)){ //show all players
            return;
        }

        if(gameStarted && !gameSetUp){ //process input for game set up (select players)
            processGameSetUpInput(line);
        }

        if(gameStarted && gameSetUp){ //process input for game
            processGameInput(line);
        }
    }

    private void processGameSetUpInput(String line) throws IOException{

        switch (line) {
            case "p": //add player
                break;

            case "r": //remove player
                break;

            case "s": //start game after players were selected
                gameSetUp = true;
                riskRound();
                break;
        }
    }

    private void processGameInput(String line) throws IOException{

        switch (line) {
            case "o": //show game overview
                System.out.print("show game overview");
                break;

            case "q": //quit game
                break;

            case "s": //save game
                break;
        }
    }


    private void riskRound() throws IOException {
        //showMenu();
        System.out.print("New round! \n ");
        //receiveUnits
        //distributeUnits
        //attack
        //defend
        //moveUnits
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
