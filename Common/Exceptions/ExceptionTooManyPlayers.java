package Common.Exceptions;

public class ExceptionTooManyPlayers extends Exception{
    public ExceptionTooManyPlayers(){
        super("No more than six players are possible.");
    }
}
