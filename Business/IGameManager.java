package Business;

import Common.Exceptions.*;

import java.io.IOException;
import java.util.List;

public interface IGameManager {
    boolean saveGame(int gameStep) throws IOException;
    void quitGame() throws IOException;
    void newGame() throws IOException;
    void startFirstRound(boolean missionRisk) throws ExceptionNotEnoughPlayer;
    int getSavedGameStep() throws IOException;

    void receiveUnits() throws ExceptionObjectDoesntExist;
    int getReceivedUnits();
    void distributeUnits(String selectedTerritory, int selectedUnits) throws ExceptionTerritorySelectedNotOwned, ExceptionTooManyUnits, ExceptionEmptyInput;
    List<Integer> attack(String attackingTerritory, String attackedTerritory, int units) throws ExceptionTerritorySelectedNotOwned, ExceptionTerritoryIsNoNeighbour, ExceptionTooLessUnits, ExceptionTooManyUnits, ExceptionEmptyInput, ExceptionOwnTerritoryAttacked;
    List<Integer> defend(String territoryToDefend, String territoryCountry, List<Integer> attackerDiceResult, int attackerUnits);
    void moveUnits(String sourceTerritory, String destinationTerritory, int units, boolean afterConquering) throws ExceptionInvolvedTerritorySelected, ExceptionTerritorySelectedNotOwned, ExceptionTooManyUnits, ExceptionTerritoryIsNoNeighbour, ExceptionEmptyInput;
}
