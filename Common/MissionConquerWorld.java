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

        List<String> playerCountries = countries.stream().map(Country::getCountryName).toList();
        List<String> worldCountries = world.stream().map(Country::getCountryName).toList();
        return ((new HashSet<>(playerCountries).containsAll(worldCountries)));
    }
}
