package me.earth.earthhack.impl.util.exception;

public class NoStackTraceException extends Exception
{
    public NoStackTraceException(String message)
    {
        super(message);
        this.setStackTrace(new StackTraceElement[0]);
    }

    public synchronized Throwable fillInStackTrace()
    {
        this.setStackTrace(new StackTraceElement[0]);
        return this;
    }

}
