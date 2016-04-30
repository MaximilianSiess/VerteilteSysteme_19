package blatt6;

// Specific implementation of generic TaskCallable interface
public class LucasCallable implements TaskCallable, java.io.Serializable {
    int n;
    
    public LucasCallable( int n ) {
        this.n = n;
    }
    
    @Override
    public Object call() throws Exception {
        int result = lucas(n);
        job.done = true;
        job.result = result;
        System.out.println("Task called. Result: " + result);
        return result;
    }
    
    private int lucas(int value) {
        if (value == 0)
                return 2;
        if (value == 1)
                return 1;
        return lucas(value - 1) + lucas(value - 2);
    }

    @Override
    public Job getJob() {
        return job;
    }
    
}
