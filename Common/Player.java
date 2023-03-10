package Common;

public class Player {
    private String playerName;
    private String playerColor;
    private Mission playerMission;

    public Player(String name, String color, Mission playerMission){

        this.playerName = name;
        this.playerColor = color;
        this.playerMission = playerMission;
    }

    public String getPlayerName(){
        return playerName;
    }

    public String getPlayerColor() { return playerColor; }

    public Mission getPlayerMission() { return playerMission; }

}
