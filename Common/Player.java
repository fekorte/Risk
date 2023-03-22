package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player {
    private final String playerName;
    private final String playerColor;
    private Mission playerMission;

    private final List<String> conqueredCountryNames;

    public Player(String name, String color){

        this.playerName = name;
        this.playerColor = color;
        conqueredCountryNames = new ArrayList<>();
    }

    public String getPlayerName(){ return playerName; }
    public String getPlayerColor(){ return playerColor; }
    public Mission getPlayerMission(){ return playerMission;}
    public void setPlayerMission(Mission playerMission){ this.playerMission = playerMission; }
    public void addConqueredCountry(String conqueredCountry){ conqueredCountryNames.add(conqueredCountry); }
    public void removeCountry(String countryName){ conqueredCountryNames.remove(countryName); }
    public List<String> getConqueredCountryNames(){ return conqueredCountryNames; }
}
