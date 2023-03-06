package Business;

public interface GameManager {

    void saveGame();
    void quitGame();
    String startFirstRound();


    String getAllCountryInfos();
    String getCountryOwner(String country);
    String getAllCountriesFromPlayer(String name);

    String getCountryNeighbours(String country);
}
