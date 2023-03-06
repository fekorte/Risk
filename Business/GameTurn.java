package Business;

public class GameTurn implements GameTurnManager {

    public GameTurn(){}

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
    public boolean isMissionSolved() {
        return false;
    }
}
