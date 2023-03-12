package Common.Exceptions;

public class ExceptionObjectDoesntExist extends Exception{
    public ExceptionObjectDoesntExist(String objectName){
        super(objectName + "doesn't exist.");
    }
}
