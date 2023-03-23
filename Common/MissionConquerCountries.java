package Common;

import java.util.List;
import java.util.Map;

public class MissionConquerCountries extends Mission{

    private final boolean twoArmies;
    private Map<String, Country> countryMap;
    public MissionConquerCountries(){

        this.twoArmies = false;
        this.missionNumber = 3;
        this.missionText = "Conquer 24 territories of your choice.";
    }
    public MissionConquerCountries(Map<String, Country> countryMap){

        this.twoArmies = true;
        this.countryMap = countryMap;
        this.missionNumber = 4;
        this.missionText = "Conquer 18 territories of your choice and occupy each with at least 2 armies.";
    }

    public boolean getTwoArmies(){ return twoArmies; }
    @Override
    public boolean isMissionCompleted(List<String> playersCountries) {

        if(!twoArmies){
            return playersCountries.size() >= 24;
        }

        int conditionTrue = 0;
        for(String country : playersCountries){
            if(countryMap.containsKey(country) && countryMap.get(country).getArmy().getUnits() >= 2){
                conditionTrue++;
            }
        }
        return conditionTrue >= 18;
    }
}
