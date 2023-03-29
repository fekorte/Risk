package Persistence;

import Common.Continent;
import Common.Territory;
import Common.Player;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IPersistence {
    void openForReading(String dataSource) throws IOException;
    void openForWriting(String dataSource) throws IOException;
    void close();
    void setGameVersion(String folderName);
    Map<String, Continent> fetchContinents() throws IOException;
    Map<String, Territory> fetchTerritories() throws IOException;
    boolean saveGameStatePlayers(List<Player> playerOrder) throws IOException;
    List<Player> fetchGameStatePlayers() throws IOException;
    boolean saveGameStateArmies(Map<String, Territory> territoryMap) throws IOException;
    Map<String, Territory> fetchGameStateArmies() throws IOException;
    void saveInvolvedTerritories(List<String> involvedTerritoryNames) throws IOException;
    List<String> fetchGameStateInvolvedTerritories() throws IOException;
    void saveGameRoundAndStep(int round, int playerTurns, int step) throws IOException;
    int[] fetchGameRoundAndStep() throws IOException;
    void resetGameState() throws IOException;
}
