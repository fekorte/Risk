package Common.Exceptions;

public class ExceptionPlayerAlreadyExists extends Exception{
    public ExceptionPlayerAlreadyExists(String playerName){
        super(playerName + " already exists, please choose a different name.");
    }
}
