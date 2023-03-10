package Common;

import java.util.List;

public class MissionConquerWorld extends Mission{
    List<Continent> continentList;

    public MissionConquerWorld(List<Continent> continentList){

        super();
        missionText = "Conquer the world.";
        this.continentList = continentList;
    }

    @Override
    public String getMissionText(){ return missionText; }

    @Override
    public boolean isMissionCompleted(String playerName) {

       if(continentList.isEmpty()){
           return false;
       }

        for(Continent continent : continentList){
            for(Country country : continent.getCountries()){
                if(!country.getArmy().getPlayer().getPlayerName().equals(playerName)){
                    return false;
                }
            }
        }
        return true;
    }
}
