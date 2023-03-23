package Business;

import Common.Continent;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface WorldManagerFriend {
    void clearWorld() throws IOException;
    Map<String, Continent> getContinents();
    int getPointsForConqueredContinents(List<String> playerCountries);
}
