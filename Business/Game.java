package Business;

import Common.Country;
import Common.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game implements GameManager {

    Map<Player, List<Country>> countryPlayerMap;
    IPlayerManager playerManager;

    public Game(IPlayerManager playerManager){

        this.playerManager = playerManager;
        countryPlayerMap = new HashMap<>();
    }

    @Override
    public void saveGame() {

    }

    @Override
    public void quitGame() {

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
        return null;
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
        return null;
    }

}
