package Common.Exceptions;

public class ExceptionTerritoryNotRecognized extends Exception{

    public ExceptionTerritoryNotRecognized(){
        super("Selected territory was not found. Please try again.");
    }
}
