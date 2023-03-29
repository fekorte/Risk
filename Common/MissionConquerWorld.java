package Common;

import java.util.HashSet;
import java.util.List;

public class MissionConquerWorld extends Mission{

    private final List<String> world;
    public MissionConquerWorld(List<String> world){

        this.world = world;
        this.missionNumber = 6;
        this.missionDescription = "Conquer the world.";
    }
    @Override
    public boolean isMissionCompleted(List<String> playersTerritories) {

        return (new HashSet<>(playersTerritories).containsAll(world));
    }
}
