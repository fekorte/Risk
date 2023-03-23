package Common.Exceptions;

public class ExceptionOwnCountryAttacked extends Exception{

    public ExceptionOwnCountryAttacked(){ super("You can't attack your own country!"); }
}
