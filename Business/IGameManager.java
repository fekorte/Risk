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

    int receiveUnits() throws ExceptionObjectDoesntExist;
    int getReceivedUnits();
    boolean allUnitsDistributed();
    void distributeUnits(String selectedCountry, int selectedUnits) throws ExceptionCountryNotOwned, ExceptionTooManyUnits, ExceptionCountryNotRecognized, ExceptionEmptyInput;
    List<Integer> attack(String attackingCountry, String attackedCountry, int units) throws ExceptionCountryNotOwned, ExceptionCountryIsNoNeighbour, ExceptionTooLessUnits, ExceptionTooManyUnits, ExceptionCountryNotRecognized, ExceptionEmptyInput, ExceptionOwnCountryAttacked;
    List<Integer> defend(String countryToDefend, String attackingCountry, List<Integer> attackerDiceResult, int attackerUnits);
    void moveUnits(String sourceCountry, String destinationCountry, int units, boolean afterConquering) throws ExceptionInvolvedCountrySelected, ExceptionCountryNotOwned, ExceptionTooManyUnits, ExceptionCountryIsNoNeighbour, ExceptionCountryNotRecognized, ExceptionEmptyInput;
}
