package Business;

import Common.Player;

public interface GameManager {


    void saveGame();
    void quitGame();
    Player startFirstRound();
    String getAllCountriesInfoPlayer(String playerName);


    int receiveUnits();
    boolean distributeUnits(String selectedCountry, int units);
    String attack(String attackingCountry, String attackedCountry, int units);
    String defend(String countryToDefend, String attackingCountry, int units);
    String moveUnits(String sourceCountry, String destinationCountry, int units);

}
