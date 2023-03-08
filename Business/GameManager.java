package Business;

public interface GameManager {


    void saveGame();
    void quitGame();
    String startFirstRound();



    int receiveUnits();
    boolean distributeUnits(String selectedCountry, int units);
    String attack(String attackingCountry, String attackedCountry, int units);
    String defend(String countryToDefend, String attackingCountry, int units);
    String moveUnits(String sourceCountry, String destinationCountry, int units);

    boolean isMissionSolved();
}
