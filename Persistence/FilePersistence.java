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
            Continent continent = new Continent(continentName, pointsForConquering, new HashMap<>());
            continents.put(continentName, continent);
        }
        close();

        //read country infos of each continent
        Map<String, List<String>> neighbours = new HashMap<>();
        Map<String, Country> countryMap = new HashMap<>();
        for(Continent continent : continents.values()){
            openForReading("Data/" + continent.getContinentName() + ".txt");
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

                Country country = new Country(countryName, abbreviation, continent.getContinentName(), new ArrayList<>(), new Army());
                continent.addCountry(country);
                countryMap.put(countryName, country);
            }
            close();
        }

        for(String countryName : neighbours.keySet()){
            for(String neighbourName : neighbours.get(countryName)){
                countryMap.get(neighbourName).addNeighbour(countryMap.get(countryName));
                countryMap.get(countryName).addNeighbour(countryMap.get(neighbourName));
            }
        }
        return continents;
    }


    @Override
    public boolean saveGameState(Map<String, Player> playerMap) throws IOException {

        openForWriting("Data/GameState.txt");
        for(Player player : playerMap.values()){
            printLine(player.getPlayerName());
            printLine(player.getPlayerColor());
            printLine(player.getPlayerMission().getMissionText());
            printLine(" ");

            printLine(String.valueOf(player.getConqueredCountries().size() * 3));
            for(Country country : player.getConqueredCountries().values()){
                printLine(country.getContinentName());
                printLine(country.getCountryName());
                printLine(String.valueOf(country.getArmy().getUnits()));
            }
        }
        close();
        return !playerMap.isEmpty();
    }



    @Override
    public Map<String, Player> fetchGameState() throws IOException {
        Map<String, Player> playerMap = new HashMap<>();
        Map<String, Continent> continents = fetchContinents();

        openForReading("Data/GameState.txt");
        if(reader == null){
            continents.clear();
        }
        while(reader != null && reader.ready()){
            String playerName = readLine();
            String color = readLine();
            String missionText = readLine();
            String gap = readLine();

            int numberOfCountries = Integer.parseInt(readLine());
            Map<String, Country> playersCountries = new HashMap<>();
            while(numberOfCountries != 0){
                String continentName = readLine();
                String countryName = readLine();
                int units = Integer.parseInt(readLine());

                continents.get(continentName).getCountryFromContinent(countryName).setArmy(new Army(units, playerMap.get(playerName)));
                playersCountries.put(countryName, continents.get(continentName).getCountryFromContinent(countryName));
                numberOfCountries -= 3;
            }
            playerMap.put(playerName, new Player(playerName, color, new MissionConquerWorld(new ArrayList<>()), playersCountries));
        }
        close();
        return playerMap;
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

        openForWriting("Data/GameState.txt");
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
