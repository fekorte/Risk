package Business;

import Common.Continent;
import Common.Country;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World implements WorldManager{

    private Map<String, Country> countryMap; //Key is the country abbreviation
    private List<Continent> continents;

    public World(){

        countryMap = new HashMap<>();
        continents = new ArrayList<>();
    }

    @Override
    public String getAllCountryInfos() {

        StringBuilder countryInfos = new StringBuilder();
        for(Country country : countryMap.values()){
            countryInfos.append(country.getCountryInfo());
        }
        return countryInfos.toString();
    }

    @Override
    public String getCountryOwner(String country){

        return countryMap.get(country).getArmy().getPlayer().getPlayerName();
    }

    @Override
    public String getAllCountriesFromPlayer(String playerName){

        StringBuilder countriesFromPlayer = new StringBuilder();
        for(Country country : countryMap.values()){
            if(country.getArmy().getPlayer().getPlayerName().equals(playerName)){
                countriesFromPlayer.append(country.getCountryName());
            }
        }
        return countriesFromPlayer.toString();
    }

    @Override
    public String getCountryNeighbours(String country){

        String neighbourInfo = "";
        for(Country neighbour : countryMap.get(country).getNeighbours()){
            neighbourInfo = neighbour.getCountryName() + " ";
        }
        return neighbourInfo;
    }
}
