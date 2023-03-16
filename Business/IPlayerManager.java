package Business;

import Common.Exceptions.ExceptionColorAlreadyExists;
import Common.Exceptions.ExceptionObjectDoesntExist;
import Common.Exceptions.ExceptionPlayerAlreadyExists;
import Common.Exceptions.ExceptionTooManyPlayer;

public interface IPlayerManager {
    void addPlayer(String name, String color) throws ExceptionPlayerAlreadyExists, ExceptionTooManyPlayer, ExceptionColorAlreadyExists;
    void removePlayer(String name) throws ExceptionObjectDoesntExist;
    String getAllowedColors();
    String getPlayersInfo();
    String getAllCountriesInfoPlayer();
    boolean playerDefeated(String playerName);
    boolean nextPlayersTurn();
    boolean continuePreviousGame();
    String getCurrentPlayerName();
    String getCurrentPlayerMission();
    int getPlayerNumber();
    int getRound();
}
