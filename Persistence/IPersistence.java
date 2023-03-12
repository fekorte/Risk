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
    List<Continent> fetchContinents() throws IOException;
    boolean saveGameStateWorld(List<Continent> continents) throws IOException;
    List<Continent> fetchGameStateWorld() throws IOException;
    boolean savePlayers(Map<String, Player> playerMap) throws IOException;
    Map<String, Player> fetchPlayers() throws IOException;

}
