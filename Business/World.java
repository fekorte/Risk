package Business;

import Common.Continent;
import Common.Country;
import Persistence.IPersistence;

import java.io.IOException;
import java.util.*;

public class World implements IWorldManager, WorldFriend{

    IPersistence persistence;
    private Map<String, Country> countryMap; //Key is the country name
    private Map<String, Continent> continents; //Key is continent name

    public World(IPersistence persistence) throws IOException {

        this.persistence = persistence;
        initialize();
    }

    public void initialize() throws IOException {

        if(!persistence.fetchGameStateWorld().isEmpty()) {
            continents = new HashMap<>(persistence.fetchGameStateWorld());
        } else {
            continents = new HashMap<>(persistence.fetchContinents());
        }
        countryMap = new HashMap<>();
        for(Continent continent : continents.values()){
            for(Country country : continent.getCountries()){
                countryMap.put(country.getCountryName(), country);
            }
        }
    }
    @Override
    public void clearWorld() throws IOException {

        continents.clear();
        countryMap.clear();
        initialize();
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
    public String getCountryNeighbours(String country){

        StringBuilder neighbourInfo = new StringBuilder();
        for(Country neighbour : countryMap.get(country).getNeighbours()){
            neighbourInfo.append("| ").append(neighbour.getCountryName()).append(" |");
        }
        return neighbourInfo.toString();
    }

    @Override
    public int getUnitAmountOfCountry(String countryName) {
        return getCountryMap().get(countryName).getArmy().getUnits();
    }
    @Override
    public Map<String, Country> getCountryMap(){ return countryMap; }
    @Override
    public List<String> getConqueredContinents(List<Country> playersCountries){

        List<String> conqueredContinents = new ArrayList<>();
        for(Continent continent : continents.values()){
            if(continent.isContinentConquered(playersCountries)){
                conqueredContinents.add(continent.getContinentName());
            }
        }
        return conqueredContinents;
    }

    @Override
    public String getCountryOwner(String country){

        return countryMap.get(country).getArmy().getPlayer().getPlayerName();
    }
    @Override
    public Map<String, Continent> getContinents() { return continents; }
}
