package Common.Exceptions;

public class ExceptionCountryNotRecognized extends Exception{

    public ExceptionCountryNotRecognized(){
        super("Selected country was not found. Please try again.");
    }
}
