package ru.sbt.jschool.session9;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutionManagerImpl implements ExecutionManager {
    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        List<Future> futureList = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(Runnable runnable : tasks){
            futureList.add(executorService.submit(runnable));
        }
        executorService.shutdown();
        new Thread(() -> runCallback(callback, executorService)).start();
        return new ContextImpl(futureList);
    }

    private void runCallback(Runnable callback, ExecutorService executorService){
        while(true){
            if (executorService.isTerminated()){
                Thread thread = new Thread(callback);
                thread.start();
                break;
            }
        }
    }
}
