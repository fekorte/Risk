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
    public boolean close(){
        if(writer != null)
            writer.close();
        if(reader != null){
            try{
                reader.close();
            } catch(IOException e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String, Continent> fetchContinents() throws IOException {

        openForReading("Data/Continents.txt");
        Map<String, Continent> continents = new HashMap<>();
        while(reader != null && reader.ready()){
            String continentName = readLine();
            int pointsForConquering = Integer.parseInt(readLine());
            Continent continent = new Continent(continentName, pointsForConquering, new ArrayList<>());
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
    public boolean saveGameStateWorld(Map<String, Continent> continentMap) throws IOException {

        openForWriting("Data/GameStateWorld.txt");
        for(Continent continent : continentMap.values()){
            for(Country country : continent.getCountries()){
                printLine(country.getCountryName());
                printLine(country.getArmy().getPlayer().getPlayerName());
                printLine(String.valueOf(country.getArmy().getUnits()));
                printLine(" ");
            }
        }
        close();

        return !continentMap.isEmpty();
    }

    @Override
    public Map<String, Continent>  fetchGameStateWorld() throws IOException {
        Map<String, Player> playerMap = fetchPlayers();
        Map<String, Continent> continents = fetchContinents();

        openForReading("Data/GameStateWorld.txt");
        while(reader != null && reader.ready()){
            for(Continent continent : continents.values()){
                for(Country country : continent.getCountries()){
                    String countryName = readLine();
                    String playerName = readLine();
                    int units = Integer.parseInt(readLine());
                    String gap = readLine();

                    country.setArmy(new Army(units, playerMap.get(playerName)));
                }
            }
        }
        close();
        return continents;
    }

    @Override
    public Map<String, Player> fetchPlayers() throws IOException {

        Map<String, Player> playerMap = new HashMap<>();
        openForReading("Data/GameStatePlayers.txt");
        while(reader != null && reader.ready()) {
            String playerName = readLine();
            String color = readLine();
            String missionText = readLine();
            String gap = readLine();

            Player player = new Player(playerName, color, new MissionConquerWorld(new ArrayList<>()));
            playerMap.put(playerName, player);
        }
        close();
        return playerMap;
    }
    @Override
    public boolean savePlayers(Map<String, Player> playerMap) throws IOException {

        openForWriting("Data/GameStatePlayers.txt");
        for(Player player : playerMap.values()){
            printLine(player.getPlayerName());
            printLine(player.getPlayerColor());
            printLine(player.getPlayerMission().getMissionText());
            printLine(" ");
        }
        close();
        return !playerMap.isEmpty();
    }

    public void resetGameState() throws IOException {

        openForWriting("Data/GameStatePlayer");
        printLine("");
        close();
        openForWriting("Data/GameStateWorld");
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
