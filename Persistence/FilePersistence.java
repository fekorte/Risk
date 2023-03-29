package Persistence;

import Common.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class FilePersistence implements IPersistence{
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    @Override
    public void openForReading(String file) throws IOException { reader = new BufferedReader(new FileReader(file)); }

    @Override
    public void openForWriting(String file) throws IOException { writer = new PrintWriter(new BufferedWriter(new FileWriter(file))); }

    @Override
    public void close(){

        if(writer != null)
            writer.close();
        if(reader != null){
            try{
                reader.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Continent> fetchContinents() throws IOException {

        openForReading("Data/Continents.txt");
        Map<String, Continent> continents = new LinkedHashMap<>();
        while(reader != null && reader.ready()){
            String continentName = readLine();
            int pointsForConquering = Integer.parseInt(readLine());
            Continent continent = new Continent(continentName, pointsForConquering);

            int numCountries = Integer.parseInt(readLine());
            while(numCountries != 0){
                String territoryName = readLine();
                continent.addTerritory(territoryName);
                numCountries--;
            }
            continents.put(continentName, continent);
        }
        close();
        return continents;
    }
    @Override
    public Map<String, Territory> fetchTerritories() throws IOException {

        Map<String, Continent> continents = fetchContinents();
        Map<String, List<String>> neighbours = new HashMap<>();
        Map<String, Territory> territoryMap = new HashMap<>();

        //read country infos of each continent
        for(String continentName : continents.keySet()){
            openForReading("Data/" + continentName + ".txt");
            while(reader != null && reader.ready()){
                String territoryName = readLine();
                String abbreviation = readLine();

                String colorString = readLine();
                String[] colorComponents = colorString.split(",");
                int r = Integer.parseInt(colorComponents[0]);
                int g = Integer.parseInt(colorComponents[1]);
                int b = Integer.parseInt(colorComponents[2]);
                Color color = new Color(r, g, b);

                int numberNeighbours = Integer.parseInt(readLine());
                List<String> neighbourList = new ArrayList<>();
                while(numberNeighbours != 0){
                    String neighbourName = readLine();
                    neighbourList.add(neighbourName);
                    numberNeighbours--;
                }
                neighbours.put(territoryName, neighbourList);

                String gap = readLine();

                Territory territory = new Territory(territoryName, abbreviation, continentName, color);
                territoryMap.put(territoryName, territory);
            }

            close();
        }

        for(String territoryName : neighbours.keySet()){
            for(String neighbourName : neighbours.get(territoryName)){
                territoryMap.get(neighbourName).addNeighbour(territoryName);
                territoryMap.get(territoryName).addNeighbour(neighbourName);
            }
        }
        return territoryMap;
    }


    @Override
    public boolean saveGameStatePlayers(List<Player> playerOrder) throws IOException {

        openForWriting("Data/GameStatePlayers.txt");
        for(Player player : playerOrder){
            printLine(player.getPlayerName());
            printLine(player.getPlayerColor());

            int missionNumber = player.getPlayerMission().getMissionNumber();
            printLine(String.valueOf(missionNumber));

            switch(missionNumber){
                case(1), (2) -> {
                    MissionConquerContinents missionConquerContinents = (MissionConquerContinents) player.getPlayerMission();
                    printLine(missionConquerContinents.getFirstContinentName());
                    printLine(missionConquerContinents.getSecondContinentName());
                    printLine(String.valueOf(missionConquerContinents.getOneMoreContinent()));
                }
                case(3), (4) -> {
                    MissionConquerTerritories missionConquerTerritories = (MissionConquerTerritories) player.getPlayerMission();
                    printLine(String.valueOf(missionConquerTerritories.getTwoArmies()));
                }
                case(5) -> {
                    MissionDefeatOpponent missionDefeatOpponent = (MissionDefeatOpponent) player.getPlayerMission();
                    printLine(missionDefeatOpponent.getOpponentName());
                }
            }

            printLine(" ");

            printLine(String.valueOf(player.getConqueredTerritoryNames().size()));
            for(String territoryName : player.getConqueredTerritoryNames()){
                printLine(territoryName);
            }
        }
        close();
        return !playerOrder.isEmpty();
    }


    @Override
    public List<Player> fetchGameStatePlayers() throws IOException {

        List<Player> playerOrder = new ArrayList<>();
        Map<String, Territory> territoryMap = fetchTerritories();
        Map<String, Continent> continentMap = new LinkedHashMap<>(fetchContinents());
        openForReading("Data/GameStatePlayers.txt");

        while(reader != null && reader.ready()){
            String playerName = readLine();
            String color = readLine();
            Player newPlayer = new Player(playerName, color);

            int missionNumber = Integer.parseInt(readLine());
            switch(missionNumber){
                case(1), (2) -> {
                    String firstContinentName = readLine();
                    String secondContinentName = readLine();
                    boolean oneMore = Boolean.parseBoolean(readLine());
                    newPlayer.setPlayerMission(new MissionConquerContinents(continentMap, firstContinentName, secondContinentName, oneMore));
                }
                case(3) -> {
                    boolean twoArmies = Boolean.parseBoolean(readLine());
                    MissionConquerTerritories missionConquerTerritories = new MissionConquerTerritories(twoArmies);
                    missionConquerTerritories.setTerritoryMap(territoryMap);
                    newPlayer.setPlayerMission(missionConquerTerritories);
                }
                case(4) -> {
                    boolean twoArmies = Boolean.parseBoolean(readLine());
                    newPlayer.setPlayerMission(new MissionConquerTerritories(twoArmies));
                }
                case(5) -> {
                    String opponentColor = readLine();
                    newPlayer.setPlayerMission(new MissionDefeatOpponent(opponentColor));
                }
                case(6) -> newPlayer.setPlayerMission(new MissionConquerWorld(new ArrayList<>(territoryMap.keySet())));
            }

            String gap = readLine();

            int numberOfCountries = Integer.parseInt(readLine());
            while(numberOfCountries != 0){
                String territoryName = readLine();
                newPlayer.addConqueredTerritory(territoryName);
                numberOfCountries--;
            }
            playerOrder.add(newPlayer);
        }
        close();
        return playerOrder;
    }

    @Override
    public boolean saveGameStateArmies(Map<String, Territory> territoryMap) throws IOException {

        openForWriting("Data/GameStateArmies.txt");
        for(Territory territory : territoryMap.values()){
            printLine(territory.getTerritoryName());
            printLine(territory.getArmy().getPlayerName());
            printLine(String.valueOf(territory.getArmy().getUnits()));
        }
        close();

        return !territoryMap.isEmpty();
    }
    @Override
    public Map<String, Territory> fetchGameStateArmies() throws IOException {

        Map<String, Territory> territoryMap = fetchTerritories();
        openForReading("Data/GameStateArmies.txt");

        if(reader == null){
            territoryMap.clear();
        }

        while(reader != null && reader.ready()){
            String territoryName = readLine();
            String owner = readLine();
            int armySize = Integer.parseInt(readLine());

            territoryMap.get(territoryName).setArmy(new Army(armySize, owner));
        }
        return territoryMap;
    }
    @Override
    public void saveInvolvedTerritories(List<String> involvedTerritoryNames) throws IOException {

        //delete previous data
        File fileInvolvedTerritories = new File("Data/GameStateInvolvedCountries.txt");
        RandomAccessFile rafInvolvedTerritories = new RandomAccessFile(fileInvolvedTerritories , "rw");
        rafInvolvedTerritories.setLength(0);
        rafInvolvedTerritories.close();

        openForWriting("Data/GameStateInvolvedCountries.txt");
        for(String involvedTerritory : involvedTerritoryNames){
            printLine(involvedTerritory);
        }
        close();
    }
    @Override
    public List<String> fetchGameStateInvolvedTerritories() throws IOException {

        List<String> involvedTerritories = new ArrayList<>();

        openForReading("Data/GameStateInvolvedCountries.txt");
        while(reader != null && reader.ready()){
            String involvedTerritory = readLine();
            involvedTerritories.add(involvedTerritory);
        }
        close();

        return involvedTerritories;
    }

    @Override
    public void saveGameRoundAndStep(int round, int playerTurns, int step) throws IOException {

        openForWriting("Data/GameRoundAndStep.txt");
        printLine(String.valueOf(round));
        printLine(String.valueOf(playerTurns));
        printLine(String.valueOf(step));
        close();
    }

    @Override
    public int[] fetchGameRoundAndStep() throws IOException {

        openForReading("Data/GameRoundAndStep.txt");
        int[] roundAndStep = new int[3];
        roundAndStep[0] = Integer.parseInt(readLine()); //int round
        roundAndStep[1] = Integer.parseInt(readLine()); //int playerTurns (more specific, several turns inside one round)
        roundAndStep[2] = Integer.parseInt(readLine()); //int steps (more specific, several steps in one turn)
        return roundAndStep;
    }

    @Override
    public void resetGameState() throws IOException {

        File filePlayers = new File("Data/GameStatePlayers.txt");
        RandomAccessFile rafPlayers = new RandomAccessFile(filePlayers, "rw");
        rafPlayers.setLength(0);
        rafPlayers.close();

        File fileArmies = new File("Data/GameStateArmies.txt");
        RandomAccessFile rafArmies = new RandomAccessFile(fileArmies, "rw");
        rafArmies.setLength(0);
        rafArmies.close();

        File fileGameRound = new File("Data/GameRoundAndStep.txt");
        RandomAccessFile rafGameRound = new RandomAccessFile(fileGameRound, "rw");
        rafGameRound.setLength(0);
        rafGameRound.close();

        File fileInvolvedTerritories = new File("Data/GameStateInvolvedCountries.txt");
        RandomAccessFile rafInvolvedTerritories = new RandomAccessFile(fileInvolvedTerritories , "rw");
        rafInvolvedTerritories.setLength(0);
        rafInvolvedTerritories.close();
    }


    private String readLine() throws IOException {

        if (reader != null)
            return reader.readLine();
        else
            return "";
    }

    private void printLine(String data) {

        if (writer != null)
            writer.println(data);
    }
}
