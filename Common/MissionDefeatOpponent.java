package Common;

import java.util.List;

public class MissionDefeatOpponent extends Mission{

    String opponentColor;

    public MissionDefeatOpponent(String opponentColor){

        this.opponentColor = opponentColor;
        this.missionText = "Destroy all " + opponentColor + " troops.";
        this.missionNumber = 6;
    }

    public String getOpponent(){ return opponentColor; }
    @Override
    public boolean isMissionCompleted(List<String> opponentsCountries) {

        return opponentsCountries.isEmpty();
    }
}
