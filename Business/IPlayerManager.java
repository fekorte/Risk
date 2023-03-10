package Business;

import Common.Player;

import java.util.Map;

public interface IPlayerManager {
    String addPlayer(String name, String color);
    boolean removePlayer(String name);
    Player nextPlayersTurn(String currentPlayer);
    String getAllowedColors();
    String getPlayersInfo();
    Map<String, Player> getPlayerMap();
    void clearPlayers();

    boolean readyToStartGame();
}
