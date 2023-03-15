package Persistence;

import Common.Continent;
import Common.Country;
import Common.Player;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IPersistence {
    void openForReading(String dataSource) throws IOException;
    void openForWriting(String dataSource) throws IOException;
    void close();
    Map<String, Continent> fetchContinents() throws IOException;
    Map<String, Country> fetchCountries() throws IOException;
    boolean saveGameStatePlayers(Map<String, Player> playerMap) throws IOException;
    Map<String, Player> fetchGameStatePlayers() throws IOException;
    Map<String, Country> fetchGameStateArmies() throws IOException;
    boolean saveGameStateArmies(Map<String, Country> countryMap) throws IOException;
    void saveGameRoundAndStep(int round, int playerTurns, int step) throws IOException;
    int[] fetchGameRoundAndStep() throws IOException;
    void resetGameState() throws IOException;
}
