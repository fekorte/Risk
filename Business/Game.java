package Business;

import Common.Army;
import Common.Country;
import Common.Player;

import java.util.*;

public class Game implements GameManager {

    Map<Player, List<Country>> countryPlayerMap;
    Map<String, Country> countryMap;
    IPlayerManager playerManager;
    IWorldManager worldManager;


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

    public String getAllCountriesFromPlayer(String playerName){

        StringBuilder playerCountries = new StringBuilder();
        for(Country country : countryPlayerMap.get(playerManager.getPlayerMap().get(playerName))){
            playerCountries.append(country.getCountryName()).append(" ");
        }
        return playerCountries.toString();
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

            countryPlayerMap.get(player).add(country);

            lastPlayer = player;
        }

        return lastPlayer;
    }

    @Override
    public int receiveUnits() {
        return 0;
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

        countryMap.get(sourceCountry).getArmy().getPlayer().getPlayerMission().setCountries(worldManager.getCountryMap());
        return null;
    }

}
