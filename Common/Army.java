package Common;

public class Army {
    private int units;
    private Player player;

    public Army(int units, Player player){

        this.units = units;
        this.player = player;
    }
    public Player getPlayer(){ return player; }
}
