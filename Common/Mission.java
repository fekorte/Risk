package Common;

import java.util.List;

public abstract class Mission {
    protected boolean isCompleted;
    protected String missionText;
    public Mission(){

        isCompleted = false;
    }
    public String getMissionText(){ return missionText; }
    public abstract boolean isMissionCompleted(List<String> playersCountries);
}
