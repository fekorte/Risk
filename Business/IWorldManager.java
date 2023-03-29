package Business;

import Common.Territory;

import java.io.IOException;
import java.util.Map;

public interface IWorldManager {
    void setWorldVersion(String selectedVersion) throws IOException;
    String getSelectedWorldVersion();
    Map<String, Territory> getTerritoryMap();
    String getWorldInfos();
    String getTerritoryNeighbours(String territoryName);
    int getUnitAmountOfTerritory(String territoryName);
    String getTerritoryOwner(String territoryName);
    String getTerritoryNameByColor(int colorRGB);
}
