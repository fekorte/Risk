package Common;

import java.util.List;

public abstract class Mission {
    protected boolean isCompleted;
    protected String missionDescription;
    protected int missionNumber;
    public Mission(){

        isCompleted = false;
    }
    public String getMissionDescription(){ return missionDescription; }
    public int getMissionNumber(){ return missionNumber; }
    public abstract boolean isMissionCompleted(List<String> playersCountries);
}
