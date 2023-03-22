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
        Map<String, Continent> continents = new HashMap<>();
        while(reader != null && reader.ready()){
            String continentName = readLine();
            int pointsForConquering = Integer.parseInt(readLine());
            Continent continent = new Continent(continentName, pointsForConquering);

            int numCountries = Integer.parseInt(readLine());
            while(numCountries != 0){
                String countryName = readLine();
                continent.addCountry(countryName);
                numCountries--;
            }
            continents.put(continentName, continent);
        }
        close();
        return continents;
    }
    @Override
    public Map<String, Country> fetchCountries() throws IOException {

        Map<String, Continent> continents = fetchContinents();
        Map<String, List<String>> neighbours = new HashMap<>();
        Map<String, Country> countryMap = new HashMap<>();

        //read country infos of each continent
        for(String continentName : continents.keySet()){
            openForReading("Data/" + continentName + ".txt");
            while(reader != null && reader.ready()){
                String countryName = readLine();
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
                neighbours.put(countryName, neighbourList);

                String gap = readLine();

                Country country = new Country(countryName, abbreviation, continentName, color);
                countryMap.put(countryName, country);
            }

            close();
        }

        for(String countryName : neighbours.keySet()){
            for(String neighbourName : neighbours.get(countryName)){
                countryMap.get(neighbourName).addNeighbour(countryName);
                countryMap.get(countryName).addNeighbour(neighbourName);
            }
        }
        return countryMap;
    }


    @Override
    public boolean saveGameStatePlayers(List<Player> playerOrder) throws IOException {

        openForWriting("Data/GameStatePlayers.txt");
        for(Player player : playerOrder){
            printLine(player.getPlayerName());
            printLine(player.getPlayerColor());
            printLine(String.valueOf(player.getPlayerMission().getMissionNumber()));
            printLine(" ");

            printLine(String.valueOf(player.getConqueredCountryNames().size()));
            for(String countryName : player.getConqueredCountryNames()){
                printLine(countryName);
            }
        }
        close();
        return !playerOrder.isEmpty();
    }


    @Override
    public List<Player> fetchGameStatePlayers() throws IOException {

        List<Player> playerOrder = new ArrayList<>();
        List<String> countryNames = new ArrayList<>(fetchCountries().keySet());
        openForReading("Data/GameStatePlayers.txt");

        while(reader != null && reader.ready()){
            String playerName = readLine();
            String color = readLine();
            int missionNumber = Integer.parseInt(readLine());
            String gap = readLine();

            Player newPlayer = new Player(playerName, color);
            newPlayer.setPlayerMission(new MissionConquerWorld(countryNames));
            int numberOfCountries = Integer.parseInt(readLine());
            while(numberOfCountries != 0){
                String countryName = readLine();
                newPlayer.addConqueredCountry(countryName);
                numberOfCountries--;
            }
            playerOrder.add(newPlayer);
        }
        close();
        return playerOrder;
    }

    @Override
    public boolean saveGameStateArmies(Map<String, Country> countryMap) throws IOException {

        openForWriting("Data/GameStateArmies.txt");
        for(Country country : countryMap.values()){
            printLine(country.getCountryName());
            printLine(country.getArmy().getPlayerName());
            printLine(String.valueOf(country.getArmy().getUnits()));
        }
        close();

        return !countryMap.isEmpty();
    }
    @Override
    public Map<String, Country> fetchGameStateArmies() throws IOException {

        Map<String, Country> countryMap = fetchCountries();
        openForReading("Data/GameStateArmies.txt");

        if(reader == null){
            countryMap.clear();
        }

        while(reader != null && reader.ready()){
            String countryName = readLine();
            String owner = readLine();
            int armySize = Integer.parseInt(readLine());

            countryMap.get(countryName).setArmy(new Army(armySize, owner));
        }
        return countryMap;
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
