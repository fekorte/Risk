package Business;

import Common.Continent;
import Common.Country;

import java.util.*;

public class World implements IWorldManager {

    private final Map<String, Country> countryMap; //Key is the country name
    private final List<Continent> continents;

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
    public String getCountryNeighbours(String country){

        String neighbourInfo = "";
        for(Country neighbour : countryMap.get(country).getNeighbours()){
            neighbourInfo = neighbour.getCountryName() + " ";
        }
        return neighbourInfo;
    }

    public Map<String, Country> getCountryMap(){ return countryMap; }

    public List<Continent> getContinents(){ return continents; }

    public List<String> getConqueredContinents(List<Country> playersCountries){

        List<String> conqueredContinents = new ArrayList<>();
        for(Continent continent : continents){
            if(continent.isContinentConquered(playersCountries)){
                conqueredContinents.add(continent.getContinentName());
            }
        }
        return conqueredContinents;
    }
}
