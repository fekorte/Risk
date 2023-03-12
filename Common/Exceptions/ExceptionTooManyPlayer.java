package Common.Exceptions;

public class ExceptionTooManyPlayer extends Exception{
    public ExceptionTooManyPlayer(){
        super("No more than six player are possible.");
    }
}
