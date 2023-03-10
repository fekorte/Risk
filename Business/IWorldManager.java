package Business;

import Common.Continent;
import Common.Country;

import java.util.List;
import java.util.Map;

public interface IWorldManager {

    String getAllCountryInfos();
    String getCountryNeighbours(String country);
    Map<String, Country> getCountryMap();
    Map<Continent, List<String>> getConqueredContinents();
}
