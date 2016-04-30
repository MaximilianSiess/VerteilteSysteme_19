package blatt6;

public interface JobInterface<T>
{
    public boolean isDone();
    public boolean isChecked();
    public T getResult();
}
