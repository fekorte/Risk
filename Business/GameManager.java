package Business;

import Common.Exceptions.*;

import java.io.IOException;
import java.util.List;

public interface GameManager {

    boolean  saveGame() throws IOException;
    void quitGame() throws IOException;
    void newGame() throws IOException;
    void startFirstRound() throws ExceptionNotEnoughPlayer;

    int receiveUnits() throws ExceptionObjectDoesntExist;
    void distributeUnits(String selectedCountry, int selectedUnits, int receivedUnits) throws ExceptionCountryNotOwned, ExceptionTooManyUnits;
    List<Integer> attack(String attackingCountry, String attackedCountry, int units) throws ExceptionCountryNotOwned, ExceptionCountryIsNoNeighbour, ExceptionTooLessUnits, ExceptionTooManyUnits;
    List<Integer> defend(String countryToDefend, String attackingCountry, List<Integer> attackerDiceResult, int attackerUnits);
    void moveUnits(String sourceCountry, String destinationCountry, int units, boolean afterConquering) throws ExceptionInvolvedCountrySelected, ExceptionCountryNotOwned, ExceptionTooManyUnits, ExceptionCountryIsNoNeighbour;
}
