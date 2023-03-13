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
    boolean nextPlayersTurn();
    String getAllowedColors();
    Map<String, Player> getPlayerMap();
    String getPlayersInfo();
    boolean isCurrentsPlayerMissionCompleted();
    boolean getContinuePreviousGame();
    void setCurrentPlayer(String currentPlayerName);
    String getCurrentPlayerName();
}
