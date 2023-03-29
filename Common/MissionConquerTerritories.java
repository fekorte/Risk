package Common;

import java.util.List;
import java.util.Map;

public class MissionConquerTerritories extends Mission{

    private final boolean twoArmies;
    private Map<String, Territory> territoryMap;

    public MissionConquerTerritories(boolean twoArmies){

        this.twoArmies = twoArmies;
        this.missionNumber = (twoArmies) ? 4 : 3;
        this.missionDescription = (twoArmies) ? "Conquer 18 territories of your choice and occupy each with at least 2 armies." : "Conquer 24 territories of your choice.";
    }

    public void setTerritoryMap(Map<String, Territory> territoryMap){ this.territoryMap = territoryMap; }
    public boolean getTwoArmies(){ return twoArmies; }
    @Override
    public boolean isMissionCompleted(List<String> playersTerritories) {

        if(!twoArmies){
            return playersTerritories.size() >= 24;
        }

        int conditionTrue = 0;
        for(String territory : playersTerritories){
            if(territoryMap.containsKey(territory) && territoryMap.get(territory).getArmy().getUnits() >= 2){
                conditionTrue++;
            }
        }
        return conditionTrue >= 18;
    }
}
