package Business;

import Common.Army;
import Common.Continent;
import Common.Territory;
import Persistence.IPersistence;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class WorldManager implements IWorldManager{

    private final IPersistence persistence;
    private Map<String, Territory> territoryMap; //Key is the country name
    private Map<String, Continent> continentMap; //Key is continent name
    private Map<Color, String> colorTerritoryNameMap;

    public WorldManager(IPersistence persistence){ this.persistence = persistence; }

    @Override
    public void setWorldVersion(String selectedVersion) throws IOException {

        persistence.setGameVersion((selectedVersion.equals("Standard Risk") ? "Data" : "LOTRData"));
        initializeWorldManager();
    }

    public void initializeWorldManager() throws IOException {

        initializeCountryMap();
        continentMap = persistence.fetchContinents();

        colorTerritoryNameMap = new HashMap<>();
        for(Territory territory : territoryMap.values()){
            colorTerritoryNameMap.put(territory.getTerritoryColor(), territory.getTerritoryName());
        }
    }

    private void initializeCountryMap() throws IOException {
        territoryMap = (persistence.fetchGameStateArmies().isEmpty()) ? persistence.fetchTerritories() : persistence.fetchGameStateArmies();
    }

    public void clearWorld() throws IOException {

        territoryMap.clear();
        initializeCountryMap();
    }
    @Override
    public String getWorldInfos() {

        StringBuilder territoryInfos = new StringBuilder();
        for(Territory territory : territoryMap.values()){
            territoryInfos.append(territory.getCountryInfo());
        }
        return territoryInfos.toString();
    }

    @Override
    public String getTerritoryNeighbours(String territoryName){

        StringBuilder neighbourInfo = new StringBuilder();
        for(String neighbourName : territoryMap.get(territoryName).getNeighbours()){
            neighbourInfo.append("| ").append(neighbourName).append(" |");
        }
        return neighbourInfo.toString();
    }

    @Override
    public int getUnitAmountOfTerritory(String territoryName) { return getTerritoryMap().get(territoryName).getArmy().getUnits(); }
    @Override
    public Map<String, Territory> getTerritoryMap(){ return territoryMap; }
    public Map<String, Continent> getContinentMap(){ return continentMap; }

    private List<String> getConqueredContinents(List<String> playerTerritories){

        List<String> conqueredTerritories = new ArrayList<>();
        for(Continent continent : continentMap.values()){
            if(continent.isContinentConquered(playerTerritories)){
                conqueredTerritories.add(continent.getContinentName());
            }
        }
        return conqueredTerritories;
    }
    public int getPointsForConqueredContinents(List<String> playerTerritories){

        int pointsForConqueredContinent = 0;
        List<String> conqueredContinents = getConqueredContinents(playerTerritories);
        for(String conqueredContinent : conqueredContinents){
            pointsForConqueredContinent += continentMap.get(conqueredContinent).getPointsForConquering();
        }
        return pointsForConqueredContinent;
    }
    @Override
    public String getTerritoryOwner(String territoryName){ return territoryMap.get(territoryName).getArmy().getPlayerName(); }
    @Override
    public String getTerritoryNameByColor(int colorRGB){ return colorTerritoryNameMap.get(new Color(colorRGB)); }
    public void addUnitsToCountry(String territoryName, int units) { territoryMap.get(territoryName).getArmy().addUnits(units); }
    public void removeUnitsFromCountry(String territoryName, int units) { territoryMap.get(territoryName).getArmy().removeUnits(units); }
    public void setCountryArmy(String territoryName, int units, String playerName){ territoryMap.get(territoryName).setArmy(new Army(units, playerName)); }
}
