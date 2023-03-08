package Common;

public class Player {
    private String playerName;
    private String playerColor;

    public Player(String name, String color){

        this.playerName = name;
        this.playerColor = color;
    }

    public String getPlayerName(){
        return playerName;
    }

    public String getPlayerColor() { return playerColor; }

}
