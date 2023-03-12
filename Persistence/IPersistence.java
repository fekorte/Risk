package Persistence;

import Common.Continent;
import Common.Player;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IPersistence {
    void openForReading(String dataSource) throws IOException;
    void openForWriting(String dataSource) throws IOException;
    boolean close();
    Map<String, Continent> fetchContinents() throws IOException;
    boolean saveGameStateWorld(Map<String, Continent> continentMap) throws IOException;
    Map<String, Continent>  fetchGameStateWorld() throws IOException;
    boolean savePlayers(Map<String, Player> playerMap) throws IOException;
    Map<String, Player> fetchPlayers() throws IOException;

}
