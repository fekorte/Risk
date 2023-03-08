package Business;

public interface IPlayerManager {
    boolean addPlayer(String name, String color);
    boolean removePlayer(String name);
    String nextPlayersTurn(String currentPlayer);
    String getAllowedColors();
    String getPlayersInfo();

    void clearPlayers();
}
