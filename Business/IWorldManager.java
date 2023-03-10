package Business;

import Common.Continent;
import Common.Country;

import java.util.Map;

public interface IWorldManager {

    String getAllCountryInfos();
    String getCountryOwner(String country);
    String getCountryNeighbours(String country);
    Map<String, Country> getCountryMap();
    void setCountryMap(Map<String, Country> countryMap);
}
