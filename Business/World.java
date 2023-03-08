package Business;

import Common.Continent;
import Common.Country;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

    private Map<String, Country> countryMap; //Key is the country abbreviation
    private List<Continent> continents;

    public World(){

        countryMap = new HashMap<>();
        continents = new ArrayList<>();
    }
}
