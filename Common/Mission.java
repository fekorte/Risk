package Common;

import java.util.List;

public abstract class Mission {
    protected boolean isCompleted;
    protected String missionText;
    protected List<String> playersCountries;
    public Mission(){ isCompleted = false; }
    public abstract String getMissionText();
    public abstract boolean isMissionCompleted();
    public void setPlayersCountries(List<String> playersCountries){ this.playersCountries = playersCountries; }
}
