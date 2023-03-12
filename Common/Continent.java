package Common;

import java.util.HashSet;
import java.util.List;

public class Continent {
    private String continentName;
    private int pointsForConquering;
    private List<Country> countries; //change it to map?
    public Continent(String continentName, int pointsForConquering, List<Country> countries){

        this.continentName = continentName;
        this.pointsForConquering = pointsForConquering;
        this.countries = countries;
    }

    public String getContinentName(){ return continentName; }
    public int getPointsForConquering(){ return pointsForConquering; }

    public List<Country> getCountries() { return countries; }
    public void addCountry(Country country){ countries.add(country); }
    public boolean isContinentConquered(List<Country> playersCountries){

        List<String> playerCountryNames = playersCountries.stream().map(Country::getCountryName).toList();
        List<String> countryNames = countries.stream().map(Country::getCountryName).toList();
        return new HashSet<>(playerCountryNames).containsAll(countryNames);
    }
}
