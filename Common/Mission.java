package Common;

import java.util.List;

public abstract class Mission {
    protected boolean isCompleted;
    protected String missionText;
    protected List<Country> countries;
    public Mission(){ isCompleted = false; }
    public abstract String getMissionText();
    public abstract boolean isMissionCompleted(String playerName);
    public void setCountries(List<Country> countries){ this.countries = countries; }
}
