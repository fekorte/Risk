package Common;

import java.util.List;

public class MissionDefeatOpponent extends Mission{

    private final String opponentName;
    public MissionDefeatOpponent(String opponentName){

        this.opponentName = opponentName;
        this.missionDescription = "Destroy all troops from " + opponentName + ".";
        this.missionNumber = 5;
    }
    public String getOpponentName(){ return opponentName; }
    @Override
    public boolean isMissionCompleted(List<String> playersTerritories) {

        return playersTerritories.isEmpty();
    }
}
