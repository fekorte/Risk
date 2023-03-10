package Common;

import java.util.List;

public class MissionConquerWorld extends Mission{

    List<Country> world;

    public MissionConquerWorld(List<Country> world){

        super();
        missionText = "Conquer the world.";
        this.world = world;
    }

    @Override
    public String getMissionText(){ return missionText; }

    @Override
    public boolean isMissionCompleted(String playerName) {

       if(countries.isEmpty()){
           return false;
       }
        for(Country country : world){
            for(Country owned : countries.values()){
                if(!country.getCountryName().equals(owned.getCountryName())){
                    return false;
                }
            }
        }

        return true;
    }
}
