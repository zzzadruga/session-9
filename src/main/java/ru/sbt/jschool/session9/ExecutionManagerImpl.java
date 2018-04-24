package ru.sbt.jschool.session9;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutionManagerImpl implements ExecutionManager {
    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        List<Future> futureList = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(Runnable runnable : tasks){
            futureList.add(executorService.submit(runnable));
        }
        new Thread(() -> shutdownAndAwaitTermination(executorService)).start();
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

    private void shutdownAndAwaitTermination(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(3, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(3, TimeUnit.MINUTES))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
