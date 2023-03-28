package Common;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Country {
    private final String countryName;
    private final String abbreviation;
    private final String continentName;
    private final Color countryColor;
    private final List<String> neighbourNames;
    private Army army;
    public Country(String countryName, String abbreviation, String continentName, Color countryColor){

        this.countryName = countryName;
        this.abbreviation = abbreviation;
        this.continentName = continentName;
        this.countryColor = countryColor;
        neighbourNames = new ArrayList<>();
    }
    public String getCountryName(){ return countryName; }
    public String getAbbreviation(){ return abbreviation; }
    public String getContinentName(){ return continentName; }
    public Color getCountryColor(){ return countryColor; }
    public List<String> getNeighbours(){ return neighbourNames; }
    public void addNeighbour(String neighbouringCountry){ neighbourNames.add(neighbouringCountry); }
    public Army getArmy(){ return army; }
    public void setArmy(Army army) { this.army = army; }
    public String getCountryInfo(){ return "Name: " + countryName + ", " + abbreviation + ", " + continentName + ", Owner: " + army.getPlayerName() + ", Army size: " + army.getUnits() + "\n"; }
}
