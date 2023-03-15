package Persistence;

import Common.*;

import java.io.*;
import java.util.*;

public class FilePersistence implements IPersistence{
    private BufferedReader reader=null;
    private PrintWriter writer=null;

    @Override
    public void openForReading(String file) throws IOException {
        reader=new BufferedReader(new FileReader(file));
    }

    @Override
    public void openForWriting(String file) throws IOException {
        writer=new PrintWriter(new BufferedWriter(new FileWriter(file)));
    }

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

                int numberNeighbours = Integer.parseInt(readLine());
                List<String> neighbourList = new ArrayList<>();
                while(numberNeighbours != 0){
                    String neighbourName = readLine();
                    neighbourList.add(neighbourName);
                    numberNeighbours--;
                }
                neighbours.put(countryName, neighbourList);

                String gap = readLine();

                Country country = new Country(countryName, abbreviation, continentName);
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
    public boolean saveGameStatePlayers(Map<String, Player> playerMap) throws IOException {

        openForWriting("Data/GameStatePlayers.txt");
        for(Player player : playerMap.values()){
            printLine(player.getPlayerName());
            printLine(player.getPlayerColor());
            //printLine(player.getPlayerMission().getMissionText());
            printLine(" ");

            printLine(String.valueOf(player.getConqueredCountryNames().size() * 3));
            for(String countryName : player.getConqueredCountryNames()){
                printLine(countryName);
            }
        }
        close();
        return !playerMap.isEmpty();
    }


    @Override
    public Map<String, Player> fetchGameStatePlayers() throws IOException {

        Map<String, Player> playerMap = new HashMap<>();

        openForReading("Data/GameStatePlayers.txt");

        while(reader != null && reader.ready()){
            String playerName = readLine();
            String color = readLine();
           // String missionText = readLine();
            String gap = readLine();

            Player newPlayer = new Player(playerName, color);

            int numberOfCountries = Integer.parseInt(readLine());
            while(numberOfCountries != 0){
                String countryName = readLine();
                newPlayer.addConqueredCountry(countryName);
                numberOfCountries -= 3;
            }
            playerMap.put(newPlayer.getPlayerName(), newPlayer);
        }
        close();
        return playerMap;
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
        roundAndStep[0] = Integer.parseInt(readLine());
        roundAndStep[1] = Integer.parseInt(readLine());
        roundAndStep[2] = Integer.parseInt(readLine());
        return roundAndStep;
    }

    @Override
    public void resetGameState() throws IOException {

        openForWriting("Data/GameStatePlayers.txt");
        printLine("");
        close();
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
