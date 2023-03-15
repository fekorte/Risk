package Common;

import java.util.HashSet;
import java.util.List;

public class MissionConquerWorld extends Mission{

    List<String> world;

    public MissionConquerWorld(List<String> world){

        super();
        missionText = "Conquer the world.";
        this.world = world;
    }

    @Override
    public String getMissionText(){ return missionText; }

    @Override
    public boolean isMissionCompleted(String playerName) {

        return (new HashSet<>(playersCountries).containsAll(world));
    }
}
