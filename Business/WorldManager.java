package Business;

import Common.Army;
import Common.Continent;
import Common.Country;
import Persistence.IPersistence;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class WorldManager implements IWorldManager{

    private final IPersistence persistence;
    private Map<String, Country> countryMap; //Key is the country name
    private final Map<String, Continent> continentMap; //Key is continent name
    private final Map<Color, String> colorCountryNameMap;

    public WorldManager(IPersistence persistence) throws IOException {

        this.persistence = persistence;
        continentMap = persistence.fetchContinents();
        initializeCountryMap();

        colorCountryNameMap = new HashMap<>();
        for(Country country : countryMap.values()){
            colorCountryNameMap.put(country.getCountryColor(), country.getCountryName());
        }
    }

    private void initializeCountryMap() throws IOException {

        countryMap = (persistence.fetchGameStateArmies().isEmpty()) ? persistence.fetchCountries() : persistence.fetchGameStateArmies();
    }
    public void clearWorld() throws IOException {

        countryMap.clear();
        initializeCountryMap();
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
    public Map<String, Continent> getContinentMap(){ return continentMap; }

    private List<String> getConqueredContinents(List<String> playerCountries){

        List<String> conqueredContinents = new ArrayList<>();
        for(Continent continent : continentMap.values()){
            if(continent.isContinentConquered(playerCountries)){
                conqueredContinents.add(continent.getContinentName());
            }
        }
        return conqueredContinents;
    }
    public int getPointsForConqueredContinents(List<String> playerCountries){

        int pointsForConqueredContinent = 0;
        List<String> conqueredContinents = getConqueredContinents(playerCountries);
        for(String conqueredContinent : conqueredContinents){
            pointsForConqueredContinent += continentMap.get(conqueredContinent).getPointsForConquering();
        }
        return pointsForConqueredContinent;
    }
    @Override
    public String getCountryOwner(String countryName){ return countryMap.get(countryName).getArmy().getPlayerName(); }
    @Override
    public String getCountryNameByColor(int colorRGB){ return colorCountryNameMap.get(new Color(colorRGB)); }
    public void addUnitsToCountry(String countryName, int units) { countryMap.get(countryName).getArmy().addUnits(units); }
    public void removeUnitsFromCountry(String countryName, int units) { countryMap.get(countryName).getArmy().removeUnits(units); }
    public void setCountryArmy(String countryName, int units, String playerName){ countryMap.get(countryName).setArmy(new Army(units, playerName)); }
}
