package Common;

import java.util.ArrayList;
import java.util.List;

public class Country {
    private String countryName;
    private String abbreviation;
    private String continentName;
    private List<String> neighbourNames;
    private Army army;

    public Country(String countryName, String abbreviation, String continentName){

        this.countryName = countryName;
        this.abbreviation = abbreviation;
        this.continentName = continentName;
        neighbourNames = new ArrayList<>();
    }
    public String getContinentName(){ return continentName; }
    public String getCountryName(){ return countryName; }

    public Army getArmy(){ return army; }

    public void setArmy(Army army) { this.army = army; }

    public List<String> getNeighbours(){ return neighbourNames; }

    public void addNeighbour(String neighbouringCountry){ neighbourNames.add(neighbouringCountry); }

    public String getCountryInfo(){ return "Name: " + countryName + ", " + abbreviation + ", " + continentName + ", Owner: " + army.getPlayerName() + ", Army size: " + army.getUnits() + "\n"; }
}
