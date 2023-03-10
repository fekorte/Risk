package Common;

public abstract class Mission {
    boolean isCompleted;
    String missionText;
    public Mission(){ isCompleted = false; }

    public abstract String getMissionText();
    public abstract boolean isMissionCompleted(String playerName);
}
