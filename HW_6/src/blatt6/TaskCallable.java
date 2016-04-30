package blatt6;

import java.util.concurrent.Callable;

// Generic Task interfaces
public interface TaskCallable extends Callable{
    public Job job = new Job();
    public Job getJob();
}
