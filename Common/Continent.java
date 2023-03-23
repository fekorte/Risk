package Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Continent {
    private final String continentName;
    private final int pointsForConquering;
    private final List<String> countryNames;
    public Continent(String continentName, int pointsForConquering){

        this.continentName = continentName;
        this.pointsForConquering = pointsForConquering;
        countryNames = new ArrayList<>();
    }

    public String getContinentName(){ return continentName; }
    public int getPointsForConquering(){ return pointsForConquering; }
    public void addCountry(String countryName){ countryNames.add(countryName); }
    public boolean isContinentConquered(List<String> playersCountries){

        return new HashSet<>(playersCountries).containsAll(countryNames);
    }
}
