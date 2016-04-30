package blatt6;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceProvider implements Service {
    ExecutorService executor = Executors.newCachedThreadPool();
    int limit;
    ArrayList<Job> jobs = new ArrayList<>();
    
    @Override
    public void setLimit (int limit) throws RemoteException {
        this.limit = limit;
    }
 
    @Override
    public <T> Job submit (TaskCallable task) throws RemoteException, Exception {
        
        // Check if previous jobs have returned
        for(int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).isDone()) {
                System.out.println("Task obsolete. Forgetting...");
                jobs.remove(i);
            }
        }
        
        // Check if limit is reached
        if (jobs.size() >= limit) {
            System.out.println("Service limit is reached! Task not accepted.");
            return null;
        }
        
        executor.submit(task);
        System.out.println("Task accepted and submitted.");
        
        Job taskjob = task.getJob();
        jobs.add(taskjob);
        return taskjob;
    }
    
    @Override   
    public void close() throws RemoteException {
        executor.shutdown();
    }
}
