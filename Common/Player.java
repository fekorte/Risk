package Common;

import java.util.Map;

public class Player {
    private String playerName;
    private String playerColor;
    private Mission playerMission;

    private Map<String, Country> conqueredCountries;

    public Player(String name, String color, Mission playerMission, Map<String, Country> conqueredCountries){

        this.playerName = name;
        this.playerColor = color;
        this.playerMission = playerMission;
        this.conqueredCountries = conqueredCountries;
    }

    public String getPlayerName(){ return playerName; }
    public String getPlayerColor(){ return playerColor; }
    public Mission getPlayerMission(){ return playerMission;}
    public void addConqueredCountry(Country conqueredCountry){ conqueredCountries.put(conqueredCountry.getCountryName(), conqueredCountry); }
    public void removeCountry(String countryName){ conqueredCountries.remove(countryName); }

    public Map<String, Country> getConqueredCountries(){ return conqueredCountries; }
    public String conqueredCountriesToString(){

        StringBuilder conqueredCountriesInfo = new StringBuilder();
        for(Country country : conqueredCountries.values()){
            conqueredCountriesInfo.append(country.getCountryName()).append(": ").append(country.getArmy().getUnits()).append("\n");
        }
        return conqueredCountriesInfo.toString();
    }
}
