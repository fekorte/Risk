package Business;

import Common.Player;

import java.util.Map;

public interface PlayerManagerFriend {
    void clearPlayers();
    Map<String, Player> getPlayerMap();
    void setCurrentPlayer(String currentPlayerName);
}
