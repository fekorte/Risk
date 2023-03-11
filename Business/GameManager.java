package Business;

import Common.Player;

import java.util.List;

public interface GameManager {

    void saveGame();
    void quitGame();
    Player startFirstRound();
    String getAllCountriesInfoPlayer(String playerName);
    String getCountryOwner(String country);


    int receiveUnits(String playerName);
    boolean distributeUnits(String selectedCountry, int units);
    List<Integer> attack(String attackingCountry, String attackedCountry, int units);
    String defend(String countryToDefend, String attackingCountry, List<Integer> attackerDiceResult, int attackerUnits);
    String moveUnits(String sourceCountry, String destinationCountry, int units);

}
