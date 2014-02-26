package de.cubeisland.engine.formatter.formatter;

public interface MappedData<T>
{
    public T getData(String raw);
    public String getKey();
}
