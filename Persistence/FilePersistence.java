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
    public List<Continent> fetchContinents() throws IOException {

        openForReading("Continents.txt");
        List<Continent> continents = new ArrayList<>();
        while(reader != null && reader.ready()){
            String continentName = readLine();
            int pointsForConquering = Integer.parseInt(readLine());
            Continent continent = new Continent(continentName, pointsForConquering, new ArrayList<>());
            continents.add(continent);
        }
        close();

        //read country infos of each continent
        Map<String, List<String>> neighbours = new HashMap<>();
        Map<String, Country> countryMap = new HashMap<>();
        for(Continent continent : continents){
            openForReading(continent + ".txt");
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
    public boolean saveGameStateWorld(List<Continent> continents) throws IOException {

        openForWriting("GameStateWorld.txt");
        for(Continent continent : continents){
            for(Country country : continent.getCountries()){
                printLine(country.getCountryName());
                printLine(country.getArmy().getPlayer().getPlayerName());
                printLine(String.valueOf(country.getArmy().getUnits()));
                printLine(" ");
            }
        }
        close();

        return !continents.isEmpty();
    }

    @Override
    public List<Continent> fetchGameStateWorld() throws IOException {
        Map<String, Player> playerMap = fetchPlayers();
        List<Continent> continents = fetchContinents();

        openForReading("GameStateWorld.txt");
        while(reader != null && reader.ready()){
            for(Continent continent : continents){
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
        openForReading("GameStatePlayers.txt");
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

        openForWriting("GameStatePlayers.txt");
        for(Player player : playerMap.values()){
            printLine(player.getPlayerName());
            printLine(player.getPlayerColor());
            printLine(player.getPlayerMission().getMissionText());
            printLine(" ");
        }
        close();
        return !playerMap.isEmpty();
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
