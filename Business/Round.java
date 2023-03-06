package Business;

public class Round implements RoundManager{
    @Override
    public void saveGame() {

    }

    @Override
    public void endGame() {

    }

    @Override
    public void startFirstRound() {

    }

    @Override
    public String getAllCountriesAndOwners() {
        return null;
    }

    @Override
    public String getCountryOwner(String country) {
        return null;
    }

    @Override
    public String getAllCountriesFromPlayer(String name) {
        return null;
    }

    @Override
    public String getCountryNeighbours(String country) {
        return null;
    }


    @Override
    public int receiveUnits() {
        return 0;
    }

    @Override
    public boolean distributeUnits(String selectedCountry, int units) {
        return false;
    }

    @Override
    public String attack(String attackingCountry, String attackedCountry, int units) {
        return null;
    }

    @Override
    public String defend(String countryToDefend, String attackingCountry, int units) {
        return null;
    }

    @Override
    public String moveUnits(String sourceCountry, String destinationCountry, int units) {
        return null;
    }

    @Override
    public String nextPlayersTurn() {
        return null;
    }

    @Override
    public boolean isMissionSolved() {
        return false;
    }
}
