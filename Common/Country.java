package Common;

import java.util.List;

public class Country {
    private String countryName;
    private String abbreviation;
    private Continent continent;
    private List<Country> neighbours; //change it to map?
    private Army army;

    public Country(String countryName, String abbreviation, Continent continent, List<Country> neighbours, Army army){

        this.countryName = countryName;
        this.abbreviation = abbreviation;
        this.continent = continent;
        this.neighbours = neighbours;
        this.army = army;
    }

    public String getCountryName(){ return countryName; }

    public String getAbbreviation(){ return abbreviation; }

    public Continent getContinent() { return continent; }

    public Army getArmy(){ return army; }

    public void setArmy(Army army) { this.army = army; }

    public List<Country> getNeighbours(){ return neighbours; }

    public String getCountryInfo(){ return "Name: " + countryName + ", " + abbreviation + ", " + continent + ", Owner: " + army.getPlayer().getPlayerName() + "\n"; }
}
