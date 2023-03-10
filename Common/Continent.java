package Common;

import java.util.List;

public class Continent {
    private String continentName;
    private List<Country> countries; //change it to map?

    public Continent(String continentName, List<Country> countries){

        this.continentName = continentName;
        this.countries = countries;
    }

    public List<Country> getCountries(){ return countries; }
}
