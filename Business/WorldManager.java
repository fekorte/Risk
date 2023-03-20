package Business;

import Common.Continent;
import Common.Country;
import Persistence.IPersistence;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class WorldManager implements IWorldManager, WorldManagerFriend {

    IPersistence persistence;
    private Map<String, Country> countryMap; //Key is the country name
    private final Map<String, Continent> continents; //Key is continent name
    private final Map<Color, String> colorCountryNameMap;

    public WorldManager(IPersistence persistence) throws IOException {

        this.persistence = persistence;
        continents = persistence.fetchContinents();
        initialize();

        colorCountryNameMap = new HashMap<>();
        for(Country country : countryMap.values()){
            colorCountryNameMap.put(country.getCountryColor(), country.getCountryName());
        }
    }

    public void initialize() throws IOException {

        countryMap = (persistence.fetchGameStateArmies().isEmpty()) ? persistence.fetchCountries() : persistence.fetchGameStateArmies();
    }
    @Override
    public void clearWorld() throws IOException {

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
        for(String neighbourName : countryMap.get(country).getNeighbours()){
            neighbourInfo.append("| ").append(neighbourName).append(" |");
        }
        return neighbourInfo.toString();
    }

    @Override
    public int getUnitAmountOfCountry(String countryName) { return getCountryMap().get(countryName).getArmy().getUnits(); }
    @Override
    public Map<String, Country> getCountryMap(){ return countryMap; }
    public Map<String, Continent> getContinents(){ return continents; }

    public List<String> getConqueredContinents(List<String> playersCountries){

        List<String> conqueredContinents = new ArrayList<>();
        for(Continent continent : continents.values()){
            if(continent.isContinentConquered(playersCountries)){
                conqueredContinents.add(continent.getContinentName());
            }
        }
        return conqueredContinents;
    }
    public int getPointsForConqueredContinents(List<String> playerCountries){

        int pointsForConqueredContinent = 0;
        List<String> conqueredContinents = getConqueredContinents(playerCountries);
        for(String conqueredContinent : conqueredContinents){
            pointsForConqueredContinent += continents.get(conqueredContinent).getPointsForConquering();
        }
        return pointsForConqueredContinent;
    }

    @Override
    public String getCountryOwner(String country){ return countryMap.get(country).getArmy().getPlayerName(); }
    @Override
    public String getCountryNameByColor(Color color){ return colorCountryNameMap.get(color); }
}
