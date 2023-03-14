package Persistence;

import Common.Continent;
import Common.Player;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IPersistence {
    void openForReading(String dataSource) throws IOException;
    void openForWriting(String dataSource) throws IOException;
    void close();
    Map<String, Continent> fetchContinents() throws IOException;
    boolean saveGameState(Map<String, Player> playerMap) throws IOException;
    Map<String, Player> fetchGameState() throws IOException;
    void saveGameRoundAndStep(int round, int playerTurns, int step) throws IOException;
    int[] fetchGameRoundAndStep() throws IOException;
    void resetGameState() throws IOException;
}
