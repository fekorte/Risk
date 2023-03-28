package Common;

public class Army {
    private int units;
    private final String playerName;
    public Army(int units, String playerName){

        this.units = units;
        this.playerName = playerName;
    }
    public int getUnits(){ return units; }
    public String getPlayerName(){ return playerName; }
    public void addUnits(int units){ this.units += units; }
    public void removeUnits(int units){ this.units -= units; }
}
