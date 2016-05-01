package blatt6_1;

import java.rmi.RemoteException;

public class Job<T> implements IJob<T>, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private IService<T> service;

	public Job(int id, IService<T> service) {
		super();
		this.id = id;
		this.service = service;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean isDone() {
		try {
			return service.isDone(id);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public T getResult() {
		try {
			return service.getResult(id);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
}
