package Business;

import Common.Continent;
import Common.Country;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface WorldFriend {
    void clearWorld() throws IOException;
    Map<String, Country> getCountryMap();
    int getPointsForConqueredContinents(List<Country> playerCountries);
}
