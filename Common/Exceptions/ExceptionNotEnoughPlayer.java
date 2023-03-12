package Common.Exceptions;

public class ExceptionNotEnoughPlayer extends Exception{
    public ExceptionNotEnoughPlayer(){
        super("At least two players are needed to play risk.");
    }
}
