package blatt6_1;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.naming.LimitExceededException;

public class ServiceProvider<T> extends Thread implements IService<T> {
	ExecutorService executor = Executors.newCachedThreadPool();
	int limit = 3;
	int currentID = 0;
	Map<Integer, Future<T>> jobs = new HashMap<Integer, Future<T>>();

	@Override
	public Job<T> submit(Callable<T> task) throws RemoteException, Exception {
		synchronized (jobs) {
			Job<T> job = new Job<T>(currentID, this);
			// Check if limit is reached
			if (jobs.size() >= limit) {
				throw new LimitExceededException();
			}

			Future<T> future = executor.submit(task);
			System.out.println("Task " + currentID + " accepted and submitted.");
			jobs.put(currentID, future);
			currentID++;
			return job;
		}
	}

	@Override
	public boolean isDone(int id) throws RemoteException {
		synchronized (jobs) {
			Iterator<Entry<Integer, Future<T>>> iterator = jobs.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Integer, Future<T>> entry = iterator.next();
				int jobID = entry.getKey();
				if (jobID == id) {
					Future<T> job = entry.getValue();
					if (job.isDone()) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
		throw new RuntimeException("Job not found!");
	}

	@Override
	public T getResult(int id) throws RemoteException {
		synchronized (jobs) {
			Iterator<Entry<Integer, Future<T>>> iterator = jobs.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Integer, Future<T>> entry = iterator.next();
				int jobID = entry.getKey();
				if (jobID == id) {
					Future<T> job = entry.getValue();
					if (job.isDone()) {
						try {
							iterator.remove();
							return job.get();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					} else {
						return null;
					}
				}
			}
		}
		throw new RuntimeException("Job not found!");
	}

	@Override
	public void close() throws RemoteException {
		executor.shutdown();
		try {
			executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
