package Common;

import java.util.List;

public class Country {
    private String countryName;
    private String abbreviation;
    private String continentName;
    private List<Country> neighbours; //change it to map?
    private Army army;

    public Country(String countryName, String abbreviation, String continentName, List<Country> neighbours, Army army){

        this.countryName = countryName;
        this.abbreviation = abbreviation;
        this.continentName = continentName;
        this.neighbours = neighbours;
        this.army = army;
    }
    public String getContinentName(){ return continentName; }
    public String getCountryName(){ return countryName; }

    public Army getArmy(){ return army; }

    public void setArmy(Army army) { this.army = army; }

    public List<Country> getNeighbours(){ return neighbours; }

    public void addNeighbour(Country neighbouringCountry){ neighbours.add(neighbouringCountry); }

    public String getCountryInfo(){ return "Name: " + countryName + ", " + abbreviation + ", " + continentName + ", Owner: " + army.getPlayerName() + ", Army size: " + army.getUnits() + "\n"; }
}
