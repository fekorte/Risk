package Common.Exceptions;

public class ExceptionInvolvedCountrySelected extends Exception{
    public ExceptionInvolvedCountrySelected(){
        super("Only countries which were not involved in this round can be selected.");
    }
}
