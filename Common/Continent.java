package Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Continent {
    private final String continentName;
    private final int pointsForConquering;
    private final List<String> territoryNames;
    public Continent(String continentName, int pointsForConquering){

        this.continentName = continentName;
        this.pointsForConquering = pointsForConquering;
        this.territoryNames = new ArrayList<>();
    }
    public String getContinentName(){ return continentName; }
    public int getPointsForConquering(){ return pointsForConquering; }
    public void addTerritory(String territoryName){ territoryNames.add(territoryName); }
    public boolean isContinentConquered(List<String> playersTerritories){

        return new HashSet<>(playersTerritories).containsAll(territoryNames);
    }
}
