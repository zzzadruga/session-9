package ru.sbt.jschool.session9;

/**
 */
public interface ExecutionManager {
    Context execute(Runnable callback, Runnable... tasks);
}
