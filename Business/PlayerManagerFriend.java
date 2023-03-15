package Business;

import Common.Country;
import Common.Player;

import java.util.List;
import java.util.Map;

public interface PlayerManagerFriend {
    void clearPlayers();
    Map<String, Player> getPlayerMap();
    void setCurrentPlayer(String currentPlayerName);
    List<Country> getCurrentPlayersCountries();
    void changeCountryOwner(String newOwnerName, String countryName);
}
