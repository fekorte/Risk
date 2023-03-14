package Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Continent {
    private String continentName;
    private int pointsForConquering;
    private Map<String, Country> countries; //change it to map?
    public Continent(String continentName, int pointsForConquering, Map<String, Country> countries){

        this.continentName = continentName;
        this.pointsForConquering = pointsForConquering;
        this.countries = countries;
    }

    public String getContinentName(){ return continentName; }
    public int getPointsForConquering(){ return pointsForConquering; }

    public Map<String, Country> getCountries(){ return countries; }
    public Country getCountryFromContinent(String countryName){ return countries.get(countryName); }
    public void addCountry(Country country){ countries.put(country.getCountryName(), country); }
    public boolean isContinentConquered(List<Country> playersCountries){

        List<String> playerCountryNames = playersCountries.stream().map(Country::getCountryName).toList();
        List<String> countryNames = new ArrayList<>(countries.keySet());
        return new HashSet<>(playerCountryNames).containsAll(countryNames);
    }
}
