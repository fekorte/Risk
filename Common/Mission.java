package Common;

public class Mission {
    boolean isCompleted;
    public Mission(){ isCompleted = false; }
    public boolean isMissionCompleted(){ return isCompleted; }
    public void setIsCompleted(boolean isCompleted){ this.isCompleted = isCompleted; }
}
