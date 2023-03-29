package Common.Exceptions;

public class ExceptionTerritoryIsNoNeighbour extends Exception{
    public ExceptionTerritoryIsNoNeighbour(String firstTerritoryName, String secondTerritoryName){
        super(firstTerritoryName + " and " + secondTerritoryName + " are not neighbours.");
    }
}
