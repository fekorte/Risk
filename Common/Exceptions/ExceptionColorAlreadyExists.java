package Common.Exceptions;

public class ExceptionColorAlreadyExists extends Exception{
    public ExceptionColorAlreadyExists(String color){
        super(color + " has already been selected by another player, please choose a different color.");
    }
}
