package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissionConquerContinents extends Mission{

    private final Map<String, Continent> continentMap;
    private final String firstContinentName;
    private final String secondContinentName;
    private final boolean oneMore;

    public MissionConquerContinents(Map<String, Continent> continentMap, String firstContinentName, String secondContinentName, boolean oneMore){

        this.missionText = (oneMore) ? "Conquer " + firstContinentName + ", " + secondContinentName + " and one other continent." : "Conquer " + firstContinentName + " and " + secondContinentName + ".";
        this.continentMap = continentMap;
        this.firstContinentName = firstContinentName;
        this.secondContinentName = secondContinentName;
        this.oneMore = oneMore;
    }

    @Override
    public boolean isMissionCompleted(List<String> playersCountries) {

        List<String> conqueredContinents =new ArrayList<>();
        for(Continent continent : continentMap.values()){
            if(continent.isContinentConquered(playersCountries)) {
                conqueredContinents.add(continent.getContinentName());
            }
        }
        return (oneMore) ? conqueredContinents.contains(firstContinentName)&& conqueredContinents.contains(secondContinentName) && conqueredContinents.size() >= 3 : conqueredContinents.contains(firstContinentName)&& conqueredContinents.contains(secondContinentName);
    }
}
