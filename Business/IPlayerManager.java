package Business;

import Common.Exceptions.ExceptionColorAlreadyExists;
import Common.Exceptions.ExceptionObjectDoesntExist;
import Common.Exceptions.ExceptionPlayerAlreadyExists;
import Common.Exceptions.ExceptionTooManyPlayer;
import Common.Player;

import java.util.Map;

public interface IPlayerManager {
    void clearPlayers();
    String addPlayer(String name, String color) throws ExceptionPlayerAlreadyExists, ExceptionTooManyPlayer, ExceptionColorAlreadyExists;
    void removePlayer(String name) throws ExceptionObjectDoesntExist;
    Player nextPlayersTurn(String currentPlayer) throws ExceptionObjectDoesntExist;
    String getAllowedColors();
    Map<String, Player> getPlayerMap();
    String getPlayersInfo();
}
