package Common.Exceptions;

public class ExceptionTooManyUnits extends Exception{
    public ExceptionTooManyUnits(int expectedAmount){
        super("Unit maximum to enter: " + expectedAmount);
    }
}
