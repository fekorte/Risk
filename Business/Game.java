package Business;

import Common.Army;
import Common.Country;
import Common.Player;

import java.util.*;

public class Game implements GameManager {

    IPlayerManager playerManager;
    IWorldManager worldManager;

    //the following two maps keep track of country ownership
    Map<Player, List<Country>> countryPlayerMap;
    Map<String, Country> countryMap;


    public Game(IPlayerManager playerManager, IWorldManager worldManager){

        this.playerManager = playerManager;
        this.worldManager = worldManager;
        countryMap = worldManager.getCountryMap();
        countryPlayerMap = new HashMap<>();
    }

    @Override
    public void saveGame() {

    }

    @Override
    public void quitGame() {

        playerManager.clearPlayers();
        countryPlayerMap.clear();
    }

    public String getAllCountriesInfoPlayer(String playerName){

        StringBuilder playerCountries = new StringBuilder();
        for(Country country : countryPlayerMap.get(playerManager.getPlayerMap().get(playerName))){
            playerCountries.append(country.getCountryName()).append(" ");
        }
        return playerCountries.toString();
    }

    @Override
    public String getCountryOwner(String country){

        return countryMap.get(country).getArmy().getPlayer().getPlayerName();
    }


    @Override
    public Player startFirstRound() {

        List<Country> countryList = (List<Country>) countryMap.values();
        List<Player> playerList = (List<Player>) playerManager.getPlayerMap().values();

        Collections.shuffle(countryList);
        Collections.shuffle(playerList);

        Player lastPlayer = null;
        for (int i = 0; i < countryList.size(); i++) {
            Country country = countryList.get(i);
            Player player = playerList.get(i % playerList.size());

            country.setArmy(new Army(1, player));

            countryMap.put(country.getCountryName(), country);
            countryPlayerMap.get(player).add(country);

            lastPlayer = player;
        }

        return lastPlayer;
    }

    @Override
    public int receiveUnits(String playerName) {

        List<Country> playerCountries = countryPlayerMap.get(playerManager.getPlayerMap().get(playerName));
        int armySize = (playerCountries.size() < 9) ? 3 : playerCountries.size() / 3;

        List<String> conqueredContinents = worldManager.getConqueredContinents(playerCountries);
        if(conqueredContinents.contains("Australia") || conqueredContinents.contains("South America")){
            armySize += 2;
        }
        if(conqueredContinents.contains("Africa")){
            armySize += 3;
        }
        if(conqueredContinents.contains("Europe") || conqueredContinents.contains("North America")){
            armySize += 5;
        }
        if(conqueredContinents.contains("Asia")){
            armySize += 7;
        }
        return armySize;
    }

    @Override
    public boolean distributeUnits(String selectedCountry, int units) {
        return false;
    }

    @Override
    public String attack(String attackingCountry, String attackedCountry, int units) {
        return null;
    }

    @Override
    public String defend(String countryToDefend, String attackingCountry, int units) {
        return null;
    }

    @Override
    public String moveUnits(String sourceCountry, String destinationCountry, int units) {

        Player currentPlayer = countryMap.get(sourceCountry).getArmy().getPlayer();
        currentPlayer.getPlayerMission().setCountries(countryPlayerMap.get(currentPlayer));
        return null;
    }

}
