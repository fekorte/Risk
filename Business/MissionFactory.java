package Business;

import Common.*;

import java.util.*;

public class MissionFactory {
    private final Map<String, Continent> continentMap;
    private final Map<String, Country> countryMap;
    private List<String> availableColors;
    private final Random random;

    public MissionFactory(Map<String, Continent> continentMap, Map<String, Country> countryMap){

        this.continentMap = continentMap;
        this.countryMap = countryMap;
        random = new Random();
    }

    public void setAvailableColors(List<String> availableColors){ this.availableColors = availableColors; }

    public Mission createMission(int missionNumber){

        switch(missionNumber){
            case(1) -> { //missionConquerContinents (two continents)
                String[] continentNames = continentMap.keySet().toArray(new String[0]);
                return new MissionConquerContinents(continentMap, continentNames[random.nextInt(continentNames.length)], continentNames[random.nextInt(continentNames.length)], false);
            }
            case(2) -> { //missionConquerContinents (two continents plus one chosen by player)
                String[] continentNames = continentMap.keySet().toArray(new String[0]);
                return new MissionConquerContinents(continentMap, continentNames[random.nextInt(continentNames.length)], continentNames[random.nextInt(continentNames.length)], true);
            }
            case (3) -> { return new MissionConquerCountries(); }

            case(4) -> { return new MissionConquerCountries(countryMap); }

            case(5) -> { //missionDefeatOpponent
                String[] colors = availableColors.toArray(new String[0]);
                return new MissionDefeatOpponent(colors[random.nextInt(colors.length)]);
            }
            case(6) -> { return new MissionConquerWorld(new ArrayList<>(countryMap.keySet())); }
        }
        return null;
    }
}

