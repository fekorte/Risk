package Common.Exceptions;

public class ExceptionCountryIsNoNeighbour extends Exception{
    public ExceptionCountryIsNoNeighbour(String firstCountryName, String secondCountryName){
        super(firstCountryName + " and " + secondCountryName + " are not neighbours.");
    }
}
