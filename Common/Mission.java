package Common;

import java.util.List;

public abstract class Mission {
    protected boolean isCompleted;
    protected String missionText;
    protected int missionNumber;
    public Mission(){

        isCompleted = false;
    }
    public String getMissionText(){ return missionText; }
    public int getMissionNumber(){ return missionNumber; }
    public abstract boolean isMissionCompleted(List<String> playersCountries);
}
