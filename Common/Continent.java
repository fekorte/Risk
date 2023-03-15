package Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Continent {
    private String continentName;
    private int pointsForConquering;
    private List<String> countryNames;
    public Continent(String continentName, int pointsForConquering){

        this.continentName = continentName;
        this.pointsForConquering = pointsForConquering;
        countryNames = new ArrayList<>();
    }

    public String getContinentName(){ return continentName; }
    public int getPointsForConquering(){ return pointsForConquering; }

    public List<String> getCountryNames(){ return countryNames; }
    public void addCountry(String countryName){ countryNames.add(countryName); }
    public boolean isContinentConquered(List<String> playersCountries){

        return new HashSet<>(playersCountries).containsAll(countryNames);
    }
}
