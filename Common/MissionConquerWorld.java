package Common;

import java.util.HashSet;
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

        return (new HashSet<>(countries).containsAll(world));
    }
}
