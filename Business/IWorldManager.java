package Business;

import Common.Country;

import java.util.Map;

public interface IWorldManager {

    String getAllCountryInfos();
    String getCountryOwner(String country);
    String getCountryNeighbours(String country);
    public Map<String, Country> getCountryMap();
    public void setCountryMap(Map<String, Country> countryMap);
}
