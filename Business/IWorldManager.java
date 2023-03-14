package Business;

public interface IWorldManager {
    String getAllCountryInfos();
    String getCountryNeighbours(String countryName);
    int getUnitAmountOfCountry(String countryName);
    String getCountryOwner(String country);
}
