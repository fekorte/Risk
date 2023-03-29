package Common.Exceptions;

public class ExceptionOwnTerritoryAttacked extends Exception{

    public ExceptionOwnTerritoryAttacked(){ super("You can't attack your own territory!"); }
}
