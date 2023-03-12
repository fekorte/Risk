package Business;

import Common.Player;

import java.util.List;

public interface GameManager {

    void saveGame();
    void quitGame();
    String getAllCountriesInfoPlayer(String playerName);
    String getCountryOwner(String country);
    Player startFirstRound();


    int receiveUnits(String playerName);
    boolean distributeUnits(String selectedCountry, int selectedUnits, int receivedUnits);
    List<Integer> attack(String attackingCountry, String attackedCountry, int units);
    List<Integer> defend(String countryToDefend, String attackingCountry, List<Integer> attackerDiceResult, int attackerUnits);
    void moveUnits(String sourceCountry, String destinationCountry, int units, boolean afterConquering);

}
