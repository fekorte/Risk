package Business;

import Common.Continent;
import Common.Country;

import java.util.*;

public class World implements IWorldManager {

    private Map<String, Country> countryMap; //Key is the country name
    private Map<Continent, List<String>> continentListMap;
    public World(){

        continentListMap = new HashMap<>();
        countryMap = new HashMap<>();
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

        String neighbourInfo = "";
        for(Country neighbour : countryMap.get(country).getNeighbours()){
            neighbourInfo = neighbour.getCountryName() + " ";
        }
        return neighbourInfo;
    }

    public Map<String, Country> getCountryMap(){ return countryMap; }

    public Map<Continent, List<String>> getConqueredContinents(){ return continentListMap; }
}
