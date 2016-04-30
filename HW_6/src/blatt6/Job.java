package blatt6;

public class Job implements JobInterface, java.io.Serializable {

    int result;
    public boolean done = false;
    public boolean wasChecked = false;
    
    @Override
    public boolean isDone() {
        return done;
    }   

    @Override
    public Object getResult() {
        if (!done) {
            return null;
        }
        wasChecked = true;
        return result;
    }

    @Override
    public boolean isChecked() {
        return wasChecked;
    }
    
}
