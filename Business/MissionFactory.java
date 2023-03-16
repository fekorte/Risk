package Business;

import Common.*;

import java.util.*;

public class MissionFactory {
    private final Map<String, Continent> continentMap;
    private final Map<String, Country> countryMap;

    List<String> availableColors;

    public MissionFactory(Map<String, Continent> continentMap, Map<String, Country> countryMap, List<String> availableColors){

        this.continentMap = continentMap;
        this.countryMap = countryMap;
        this.availableColors = availableColors;
    }

    public Mission createMission(String playerColor, int missionNumber){

        switch(missionNumber){
            case(1) -> { //missionConquerContinents (two continents)
                Random random = new Random();
                String[] continentNames = continentMap.keySet().toArray(new String[0]);
                return new MissionConquerContinents(continentMap, continentNames[random.nextInt(continentNames.length)], continentNames[random.nextInt(continentNames.length)], false);
            }
            case(2) -> { //missionConquerContinents (two continents plus one chosen by player)
                Random random = new Random();
                String[] continentNames = continentMap.keySet().toArray(new String[0]);
                return new MissionConquerContinents(continentMap, continentNames[random.nextInt(continentNames.length)], continentNames[random.nextInt(continentNames.length)], true);
            }
            case (3) -> { return new MissionConquerCountries(); }

            case(4) -> { return new MissionConquerCountries(countryMap); }

            case(5) -> {
                Random random = new Random();
                String[] colors = availableColors.toArray(new String[0]);
                String selectedColor;
                do{
                    selectedColor = colors[random.nextInt(colors.length)];
                } while (selectedColor.equals(playerColor));

                return new MissionDefeatOpponent(selectedColor);
            }
            case(6) -> { return new MissionConquerWorld(new ArrayList<>(countryMap.keySet())); }
        }
        return null;
    }
}

