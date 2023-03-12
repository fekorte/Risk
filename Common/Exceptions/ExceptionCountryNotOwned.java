package Common.Exceptions;

public class ExceptionCountryNotOwned extends Exception{
    public ExceptionCountryNotOwned(String countryName, String playerName){
        super(playerName + " does not own " + countryName + ".");
    }
}
