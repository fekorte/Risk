package Business;

import Common.Exceptions.*;

import java.util.List;

public interface IPlayerManager {
    void addPlayer(String name, String color) throws ExceptionPlayerAlreadyExists, ExceptionTooManyPlayers, ExceptionColorAlreadyExists, ExceptionEmptyInput;
    void removePlayer(String name) throws ExceptionObjectDoesntExist, ExceptionEmptyInput;
    List<String> getAllowedColors();
    String getPlayerColor(String playerName);
    String getPlayersInfo();
    List<String> getPlayerNames();
    List<String> getAllCountriesInfoPlayer(String playerName);
    boolean isPlayerDefeated(String playerName);
    void nextPlayersTurn();
    String isAnyMissionCompleted();
    boolean continuePreviousGame();
    String getCurrentPlayerName();
    String getPlayerMission(String playerName);
    int getPlayerAmount();
    int getRound();
}
