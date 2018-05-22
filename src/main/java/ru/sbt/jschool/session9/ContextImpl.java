package ru.sbt.jschool.session9;

import java.util.Collection;
import java.util.concurrent.*;

public class ContextImpl implements Context {
    private Collection<Future> futureList;
    public ContextImpl(Collection<Future> futureList){
        this.futureList = futureList;
    }

    @Override
    public int getCompletedTaskCount() {
        int[] t = new int[1];
        futureList.stream().filter(Future::isDone)
        .forEach((v) -> {
            try {
                v.get();
                t[0]++;
            } catch (Exception e) {
                //e.printStackTrace();
            }
        });
        return t[0];
    }

    @Override
    public int getFailedTaskCount() {
        int[] t = new int[1];
        futureList.stream().filter(Future::isDone).forEach(v -> {
            try {
                v.get();
            } catch (ExecutionException e) {
                t[0]++;
                //e.printStackTrace();
            } catch (Exception e) {
                //e.printStackTrace();
            }});
        return t[0];
    }

    @Override
    public int getInterruptedTaskCount() {
        return (int) futureList.stream().filter(Future::isCancelled).count();
    }

    @Override
    public void interrupt() {
        futureList.forEach((v) -> v.cancel(false));
    }

    @Override
    public boolean isFinished() {
        return futureList.size() == (getCompletedTaskCount() + getInterruptedTaskCount());
    }
}
