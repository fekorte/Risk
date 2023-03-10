package Business;

import Common.Continent;
import Common.Country;

import java.util.HashMap;
import java.util.Map;

public class World implements IWorldManager {

    private Map<String, Country> countryMap; //Key is the country name


    public World(){

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
    public String getCountryOwner(String country){

        return countryMap.get(country).getArmy().getPlayer().getPlayerName();
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

    public void setCountryMap(Map<String, Country> countryMap){ this.countryMap = countryMap; }
}
