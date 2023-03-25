package Business;

import Common.*;

import java.util.*;

public class MissionFactory {
    private final Map<String, Continent> continentMap;
    private final Map<String, Country> countryMap;
    private List<String> opponents;
    private final Random random;

    public MissionFactory(Map<String, Continent> continentMap, Map<String, Country> countryMap){

        this.continentMap = continentMap;
        this.countryMap = countryMap;
        random = new Random();
    }

    public void setOpponents(List<String> opponents){ this.opponents = opponents; }

    public Mission createMission(int missionNumber){

        switch(missionNumber){
            case(1) -> { //missionConquerContinents (two continents)

                List<String> continentNames = new ArrayList<>(continentMap.keySet());
                Collections.sort(continentNames);
                List<List<String>> continentPairs = new ArrayList<>();
                continentPairs.add(Arrays.asList(continentNames.get(4), continentNames.get(0)));
                continentPairs.add(Arrays.asList(continentNames.get(1), continentNames.get(5)));
                continentPairs.add(Arrays.asList(continentNames.get(4), continentNames.get(2)));

                List<String> selectedPair = continentPairs.get(random.nextInt(continentPairs.size()));
                return new MissionConquerContinents(continentMap, selectedPair.get(0), selectedPair.get(1), false);
            }
            case(2) -> { //missionConquerContinents (two continents plus one chosen by player)

                List<String> continentNames = new ArrayList<>(continentMap.keySet());
                Collections.sort(continentNames);
                List<List<String>> continentPairs = new ArrayList<>();
                continentPairs.add(Arrays.asList(continentNames.get(3), continentNames.get(2)));
                continentPairs.add(Arrays.asList(continentNames.get(3), continentNames.get(5)));

                List<String> selectedPair = continentPairs.get(random.nextInt(continentPairs.size()));
                return new MissionConquerContinents(continentMap, selectedPair.get(0), selectedPair.get(1), true);
            }
            case (3), (4) -> { //missionConquerCountries, case 3 = 24 territories, case 4 = 18 territories, each at least 2 armies
                MissionConquerCountries missionConquerCountries = (missionNumber == 3) ? new MissionConquerCountries(false) : new MissionConquerCountries(true);
                missionConquerCountries.setCountryMap(countryMap);
                return missionConquerCountries;
            }

            case(5) -> { //missionDefeatOpponent
                String[] possibleOpponents = opponents.toArray(new String[0]);
                return new MissionDefeatOpponent(possibleOpponents[random.nextInt(possibleOpponents.length)]);
            }
            case(6) -> { return new MissionConquerWorld(new ArrayList<>(countryMap.keySet())); }
        }
        return null;
    }
}

