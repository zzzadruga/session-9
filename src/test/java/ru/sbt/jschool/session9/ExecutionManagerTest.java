package ru.sbt.jschool.session9;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ExecutionManagerTest {
    @Test
    public void completedTaskCountTest() {
        ExecutionManager executionManager = new ExecutionManagerImpl();
        Context context = executionManager.execute(System.out::println, (Runnable) () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        assertEquals(0, context.getCompletedTaskCount());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, context.getCompletedTaskCount());
    }

    @Test
    public void failedTaskCountTest() {
        ExecutionManager executionManager = new ExecutionManagerImpl();
        Context context = executionManager.execute(System.out::println, (Runnable) () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                throw new NullPointerException();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        assertEquals(0, context.getFailedTaskCount());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, context.getFailedTaskCount());
    }


    @Test
    public void interruptedTaskCountTest() {
        ExecutionManager executionManager = new ExecutionManagerImpl();
        Context context = executionManager.execute(System.out::println, (Runnable) () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        assertEquals(0, context.getInterruptedTaskCount());
        context.interrupt();
        assertEquals(1, context.getInterruptedTaskCount());
    }

    @Test
    public void finishedTest() {
        final AtomicInteger atomicInt = new AtomicInteger(0);
        Runnable[] runnables = new Runnable[5];
        for (int i = 0; i < 5; i++) {
            runnables[i] = () -> {
                try {
                    TimeUnit.SECONDS.sleep(atomicInt.incrementAndGet());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }
        ExecutionManager executionManager = new ExecutionManagerImpl();
        Context context = executionManager.execute(System.out::println, runnables);
        assertEquals(false, context.isFinished());
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.interrupt();
        assertEquals(true, context.isFinished());
        assertEquals(5, context.getCompletedTaskCount() + context.getInterruptedTaskCount());
    }

    @Test
    public void allTaskCountTest() throws InterruptedException {
        final AtomicInteger atomicInt = new AtomicInteger(0);
        Runnable[] runnables = new Runnable[6];
        for (int i = 0; i < 5; i++) {
            runnables[i] = () -> {
                try {
                    if (atomicInt.getAndIncrement() < 3) {
                        TimeUnit.SECONDS.sleep(1);
                    } else {
                        TimeUnit.SECONDS.sleep(3);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }
        runnables[5] = () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                throw new NullPointerException();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        ExecutionManager executionManager = new ExecutionManagerImpl();
        Context context = executionManager.execute(System.out::println, runnables);
        assertEquals(false, context.isFinished());
        assertEquals(0, context.getCompletedTaskCount());
        assertEquals(0, context.getFailedTaskCount());
        assertEquals(0, context.getInterruptedTaskCount());
        TimeUnit.SECONDS.sleep(2);
        assertEquals(false, context.isFinished());
        assertEquals(3, context.getCompletedTaskCount());
        assertEquals(1, context.getFailedTaskCount());
        assertEquals(0, context.getInterruptedTaskCount());
        context.interrupt();
        TimeUnit.SECONDS.sleep(2);
        assertEquals(false, context.isFinished());
        assertEquals(3, context.getCompletedTaskCount());
        assertEquals(1, context.getFailedTaskCount());
        assertEquals(2, context.getInterruptedTaskCount());
    }
}
