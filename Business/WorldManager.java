package Business;

public interface WorldManager {

    String getAllCountryInfos();
    String getCountryOwner(String country);
    String getAllCountriesFromPlayer(String playerName);
    String getCountryNeighbours(String country);
}
