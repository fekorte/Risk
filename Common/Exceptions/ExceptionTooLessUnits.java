package Common.Exceptions;

public class ExceptionTooLessUnits extends Exception{
    public ExceptionTooLessUnits(int expectedAmount){
        super("Unit minimum to enter: " + expectedAmount);
    }
}
