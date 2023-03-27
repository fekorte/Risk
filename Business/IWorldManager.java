package Business;

import Common.Country;

import java.awt.*;
import java.util.Map;

public interface IWorldManager {
    Map<String, Country> getCountryMap();
    String getAllCountryInfos();
    String getCountryNeighbours(String countryName);
    int getUnitAmountOfCountry(String countryName);
    String getCountryOwner(String country);
    String getCountryNameByColor(Color color);
}
