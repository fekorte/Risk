package Business;

import Common.Territory;

import java.util.Map;

public interface IWorldManager {
    Map<String, Territory> getTerritoryMap();
    String getWorldInfos();
    String getTerritoryNeighbours(String territoryName);
    int getUnitAmountOfTerritory(String territoryName);
    String getTerritoryOwner(String territoryName);
    String getTerritoryNameByColor(int colorRGB);
}
