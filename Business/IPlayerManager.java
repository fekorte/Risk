package Business;

public interface IPlayerManager {
    boolean addPlayer(String name, String color);
    boolean removePlayer(String name);
    String getPlayersInfo();
    String nextPlayersTurn();
}
