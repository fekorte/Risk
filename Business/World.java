package Business;

import Common.Army;
import Common.Continent;
import Common.Country;
import Persistence.FilePersistence;
import Persistence.IPersistence;

import java.io.IOException;
import java.util.*;

public class World implements IWorldManager {

    private final Map<String, Country> countryMap; //Key is the country name
    private final Map<String, Continent> continents; //Key is continent name

    public World() throws IOException {

        IPersistence persistence = new FilePersistence();
        continents = new HashMap<>(persistence.fetchContinents());

        countryMap = new HashMap<>();
        for(Continent continent : continents.values()){
            for(Country country : continent.getCountries()){
                countryMap.put(country.getCountryName(), country);
            }
        }
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
        for(Continent continent : continents.values()){
            if(continent.isContinentConquered(playersCountries)){
                conqueredContinents.add(continent.getContinentName());
            }
        }
        return conqueredContinents;
    }

    public Map<String, Continent> getContinents() { return continents; }
}
