package Common;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String playerName;
    private final String playerColor;
    private Mission playerMission;
    private final List<String> conqueredTerritoryNames;

    public Player(String name, String color){

        this.playerName = name;
        this.playerColor = color;
        conqueredTerritoryNames = new ArrayList<>();
    }
    public String getPlayerName(){ return playerName; }
    public String getPlayerColor(){ return playerColor; }
    public Mission getPlayerMission(){ return playerMission;}
    public void setPlayerMission(Mission playerMission){ this.playerMission = playerMission; }
    public List<String> getConqueredTerritoryNames(){ return conqueredTerritoryNames; }
    public void addConqueredTerritory(String conqueredTerritoryName){ conqueredTerritoryNames.add(conqueredTerritoryName); }
    public void removeTerritory(String territoryName){ conqueredTerritoryNames.remove(territoryName); }
}
