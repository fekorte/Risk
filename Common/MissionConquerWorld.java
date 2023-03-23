package Common;

import java.util.HashSet;
import java.util.List;

public class MissionConquerWorld extends Mission{

    private final List<String> world;
    public MissionConquerWorld(List<String> world){

        this.world = world;
        this.missionNumber = 6;
        this.missionText = "Conquer the world.";
    }
    @Override
    public boolean isMissionCompleted(List<String> playersCountries) {

        return (new HashSet<>(playersCountries).containsAll(world));
    }
}
