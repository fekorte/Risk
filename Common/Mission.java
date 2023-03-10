package Common;

import java.util.Map;

public abstract class Mission {
    protected boolean isCompleted;
    protected String missionText;
    protected Map<Continent, Country> world;
    protected Map<String, Country> countries;
    public Mission(){ isCompleted = false; }
    public abstract String getMissionText();
    public abstract boolean isMissionCompleted(String playerName);
    public void setWorld(Map<Continent, Country> world){ this.world = world; }

}
