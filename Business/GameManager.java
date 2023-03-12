package Business;

import Common.Exceptions.*;
import Common.Player;

import java.util.List;

public interface GameManager {

    void saveGame();
    void quitGame();
    String getAllCountriesInfoPlayer(String playerName);
    String getCountryOwner(String country);
    Player startFirstRound() throws ExceptionNotEnoughPlayer;


    int receiveUnits(String playerName) throws ExceptionObjectDoesntExist;
    void distributeUnits(String selectedCountry, int selectedUnits, int receivedUnits) throws ExceptionCountryNotOwned, ExceptionTooManyUnits;
    List<Integer> attack(String attackingCountry, String attackedCountry, int units) throws ExceptionCountryNotOwned, ExceptionCountryIsNoNeighbour, ExceptionTooLessUnits, ExceptionTooManyUnits;
    List<Integer> defend(String countryToDefend, String attackingCountry, List<Integer> attackerDiceResult, int attackerUnits);
    void moveUnits(String sourceCountry, String destinationCountry, int units, boolean afterConquering) throws ExceptionInvolvedCountrySelected, ExceptionCountryNotOwned, ExceptionTooManyUnits, ExceptionCountryIsNoNeighbour;

}
