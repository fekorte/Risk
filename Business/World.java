package Business;

import Common.Army;
import Common.Continent;
import Common.Country;

import java.util.*;

public class World implements IWorldManager {

    private final Map<String, Country> countryMap; //Key is the country name
    private final List<Continent> continents;

    public World(){

        List<Country> neighbours = new ArrayList<>();
        List<Country> neighbours2 = new ArrayList<>();
        Country country = new Country("Germany", "DE", new Continent("Europe", neighbours), neighbours, new Army());
        Country country2 = new Country("Greece", "G", new Continent("Europe", neighbours2), neighbours2, new Army());
        country.addNeighbour(country2);
        country2.addNeighbour(country);

        countryMap = new HashMap<>();
        countryMap.put(country.getCountryName(), country);
        countryMap.put(country2.getCountryName(), country2);

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
