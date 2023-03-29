package Common.Exceptions;

public class ExceptionInvolvedTerritorySelected extends Exception{
    public ExceptionInvolvedTerritorySelected(){ super("Only territories which were not involved in this round can be selected."); }
}
