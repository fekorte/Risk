package Common;

public class Army {
    private int units;
    private String playerName;

    public Army(){}
    public Army(int units, String playerName){

        this.units = units;
        this.playerName = playerName;
    }

    public String getPlayerName(){ return playerName; }

    public int getUnits(){ return units; }

    public void addUnits(int units){ this.units += units; }

    public void removeUnits(int units){ this.units -= units; }
}
