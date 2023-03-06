package Business;

public interface RoundManager {

    void saveGame();
    void endGame();


    void startFirstRound();

    String getAllCountryAndOwner();
    String getCountryOwner(String country);
    String getAllCountriesFromPlayer(String name);

    String getCountryNeighbours(String country);



    int receiveUnits();
    boolean distributeUnits(String selectedCountry, int units);
    String attack(String attackingCountry, String attackedCountry, int units);
    String defend(String countryToDefend, String attackingCountry, int units);
    String moveUnits(String sourceCountry, String destinationCountry, int units);
    String nextPlayersTurn();

    boolean isMissionSolved();
}
