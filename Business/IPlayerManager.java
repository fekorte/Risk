package Business;

import Common.Player;

public interface IPlayerManager {
    String addPlayer(String name, String color);
    boolean removePlayer(String name);
    Player nextPlayersTurn(String currentPlayer);
    String getAllowedColors();
    String getPlayersInfo();
    void clearPlayers();

    boolean readyToStartGame();
}
