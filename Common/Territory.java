package Common;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Territory {
    private final String territoryName;
    private final String abbreviation;
    private final String continentName;
    private final Color territoryColor;
    private final List<String> neighbourNames;
    private Army army;
    public Territory(String territoryName, String abbreviation, String continentName, Color territoryColor){

        this.territoryName = territoryName;
        this.abbreviation = abbreviation;
        this.continentName = continentName;
        this.territoryColor = territoryColor;
        neighbourNames = new ArrayList<>();
    }
    public String getTerritoryName(){ return territoryName; }
    public String getAbbreviation(){ return abbreviation; }
    public String getContinentName(){ return continentName; }
    public Color getTerritoryColor(){ return territoryColor; }
    public List<String> getNeighbours(){ return neighbourNames; }
    public void addNeighbour(String neighbouringCountry){ neighbourNames.add(neighbouringCountry); }
    public Army getArmy(){ return army; }
    public void setArmy(Army army) { this.army = army; }
    public String getCountryInfo(){ return "Name: " + territoryName + ", " + abbreviation + ", " + continentName + ", Owner: " + army.getPlayerName() + ", Army size: " + army.getUnits() + "\n"; }
}
