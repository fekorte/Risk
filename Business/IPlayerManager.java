package Business;

import Common.Exceptions.ExceptionColorAlreadyExists;
import Common.Exceptions.ExceptionObjectDoesntExist;
import Common.Exceptions.ExceptionPlayerAlreadyExists;
import Common.Exceptions.ExceptionTooManyPlayer;

import java.io.IOException;

public interface IPlayerManager {
    boolean saveGame(int gameStep)  throws IOException;
    String addPlayer(String name, String color) throws ExceptionPlayerAlreadyExists, ExceptionTooManyPlayer, ExceptionColorAlreadyExists;
    void removePlayer(String name) throws ExceptionObjectDoesntExist;
    String getAllowedColors();
    String getPlayersInfo();
    String getAllCountriesInfoPlayer(String playerName);
    boolean nextPlayersTurn();
    boolean continuePreviousGame();
    String getCurrentPlayerName();
    int getPlayerNumber();
    int getRound();
}
