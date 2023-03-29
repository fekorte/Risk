package Common.Exceptions;

public class ExceptionTerritorySelectedNotOwned extends Exception{
    public ExceptionTerritorySelectedNotOwned(String selectedTerritoryName, String playerName){
        super(playerName + " does not own " + selectedTerritoryName + ".");
    }
}
