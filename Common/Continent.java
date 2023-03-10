package Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Continent {
    private String continentName;
    private List<Country> countries; //change it to map?
    public Continent(String continentName, List<Country> countries){

        this.continentName = continentName;
        this.countries = countries;
    }

    public String getContinentName(){ return continentName; }
    public boolean isContinentConquered(List<Country> playersCountries){

        List<String> playerCountryNames = playersCountries.stream().map(Country::getCountryName).toList();
        List<String> countryNames = countries.stream().map(Country::getCountryName).toList();
        return new HashSet<>(playerCountryNames).containsAll(countryNames);
    }
}
