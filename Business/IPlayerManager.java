package Business;

import Common.Player;

import java.util.Map;

public interface IPlayerManager {
    void clearPlayers();
    boolean readyToStartGame();
    String addPlayer(String name, String color);
    void removePlayer(String name);
    Player nextPlayersTurn(String currentPlayer);
    String getAllowedColors();
    Map<String, Player> getPlayerMap();
    String getPlayersInfo();
}
