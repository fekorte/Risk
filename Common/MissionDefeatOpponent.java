package Common;

import java.util.List;

public class MissionDefeatOpponent extends Mission{

    private final String opponentName;
    public MissionDefeatOpponent(String opponentColor){

        this.opponentName = opponentColor;
        this.missionText = "Destroy all troops from " + opponentColor + ".";
        this.missionNumber = 5;
    }
    public String getOpponentName(){ return opponentName; }
    @Override
    public boolean isMissionCompleted(List<String> opponentsCountries) {

        return opponentsCountries.isEmpty();
    }
}
