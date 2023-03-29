package Business;

import Common.Exceptions.*;

import java.util.List;

public interface IPlayerManager {
    boolean getContinuePreviousGame();
    void addPlayer(String name, String color) throws ExceptionPlayerAlreadyExists, ExceptionTooManyPlayers, ExceptionColorAlreadyExists, ExceptionEmptyInput;
    void removePlayer(String name) throws ExceptionObjectDoesntExist, ExceptionEmptyInput;
    String getPlayerColor(String playerName);
    List<String> getAllowedColors();
    String getCurrentPlayerName();
    List<String> getPlayerNames();
    String getPlayerMission(String playerName);
    int getPlayerAmount();
    int getRound();
    List<String> getAllTerritoryInfosPlayer(String playerName);
    String getPlayersInfo();
    String isAnyMissionCompleted();
    boolean isPlayerDefeated(String playerName);
    void nextPlayersTurn();
}
