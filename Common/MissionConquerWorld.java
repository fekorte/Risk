package Common;

public class MissionConquerWorld extends Mission{

    public MissionConquerWorld(){

        super();
        missionText = "Conquer the world.";
    }

    @Override
    public String getMissionText(){ return missionText; }

    @Override
    public boolean isMissionCompleted(String playerName) {

       if(world.isEmpty()){
           return false;
       }

        for(Continent continent : world.keySet()){
            for(Country country : continent.getCountries()){
                if(!country.getArmy().getPlayer().getPlayerName().equals(playerName)){
                    return false;
                }
            }
        }
        return true;
    }
}
