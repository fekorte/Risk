package Common;

public class Army {
    private int units;
    private Player player;

    public Army(){}
    public Army(int units, Player player){

        this.units = units;
        this.player = player;
    }

    public Player getPlayer(){ return player; }

    public int getUnits(){ return units; }

    public void addUnits(int units){ this.units += units; }
}
