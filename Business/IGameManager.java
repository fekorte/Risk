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
    void distributeUnits(String selectedCountry, int selectedUnits, int receivedUnits) throws ExceptionCountryNotOwned, ExceptionTooManyUnits, ExceptionCountryNotRecognized;
    List<Integer> attack(String attackingCountry, String attackedCountry, int units) throws ExceptionCountryNotOwned, ExceptionCountryIsNoNeighbour, ExceptionTooLessUnits, ExceptionTooManyUnits, ExceptionCountryNotRecognized;
    List<Integer> defend(String countryToDefend, String attackingCountry, List<Integer> attackerDiceResult, int attackerUnits);
    void moveUnits(String sourceCountry, String destinationCountry, int units, boolean afterConquering) throws ExceptionInvolvedCountrySelected, ExceptionCountryNotOwned, ExceptionTooManyUnits, ExceptionCountryIsNoNeighbour, ExceptionCountryNotRecognized;
}
