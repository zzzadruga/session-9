package ru.sbt.jschool.session9;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ContextImpl implements Context {
    private List<Future> futureList;
    public ContextImpl(List<Future> futureList){
        this.futureList = futureList;
    }

    @Override
    public int getCompletedTaskCount() {
        final AtomicInteger completedTaskCount = new AtomicInteger();
        futureList.forEach((v) -> {
            try {
                v.get();
                completedTaskCount.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return completedTaskCount.get();
    }

    @Override
    public int getFailedTaskCount() {
        final AtomicInteger failedTaskCount = new AtomicInteger();
        futureList.forEach((v) -> {
            try {
                v.get();
            } catch (ExecutionException e) {
                failedTaskCount.incrementAndGet();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return failedTaskCount.get();
    }

    @Override
    public int getInterruptedTaskCount() {
        return (int) futureList.stream().filter(Future::isCancelled).count();
    }

    @Override
    public void interrupt() {
        futureList.forEach(Future::isCancelled);
    }

    @Override
    public boolean isFinished() {
        return futureList.size() == (getCompletedTaskCount() + getInterruptedTaskCount());
    }
}
