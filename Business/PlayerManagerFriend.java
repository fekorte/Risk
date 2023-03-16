package Business;

import Common.Player;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PlayerManagerFriend {
    boolean save(int gameStep)  throws IOException;
    void clearPlayers();
    Map<String, Player> getPlayerMap();
    void setCurrentPlayer(String currentPlayerName);
    List<String> getCurrentPlayersCountries();
    void changeCountryOwner(String newOwnerName, String previousOwnerName, String countryName);
    void setPlayerMission();
}
