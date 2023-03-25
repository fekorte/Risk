package Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissionConquerContinents extends Mission{

    private final Map<String, Continent> continentMap;
    private final String firstContinentName;
    private final String secondContinentName;
    private final boolean oneMoreContinent;

    public MissionConquerContinents(Map<String, Continent> continentMap, String firstContinentName, String secondContinentName, boolean oneMoreContinent){

        this.missionNumber = (!oneMoreContinent) ? 1 : 2;
        this.missionText = (!oneMoreContinent) ? "Conquer " + firstContinentName + " and " + secondContinentName + "." : "Conquer " + firstContinentName + ", " + secondContinentName + " and one other continent.";
        this.continentMap = continentMap;
        this.firstContinentName = firstContinentName;
        this.secondContinentName = secondContinentName;
        this.oneMoreContinent = oneMoreContinent;
    }
    public String getFirstContinentName(){ return firstContinentName; }
    public String getSecondContinentName(){ return secondContinentName; }
    public boolean getOneMore(){ return oneMoreContinent; }
    @Override
    public boolean isMissionCompleted(List<String> playersCountries) {

        List<String> conqueredContinents =new ArrayList<>();
        for(Continent continent : continentMap.values()){
            if(continent.isContinentConquered(playersCountries)) {
                conqueredContinents.add(continent.getContinentName());
            }
        }
        return (oneMoreContinent) ? conqueredContinents.contains(firstContinentName)&& conqueredContinents.contains(secondContinentName) && conqueredContinents.size() >= 3 : conqueredContinents.contains(firstContinentName)&& conqueredContinents.contains(secondContinentName);
    }
}
