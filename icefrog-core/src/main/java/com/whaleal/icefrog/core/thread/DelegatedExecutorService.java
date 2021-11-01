package com.whaleal.icefrog.core.thread;

import com.whaleal.icefrog.core.lang.Precondition;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * ExecutorService代理
 *
 * @author Looly
 * @author wh
 */
public class DelegatedExecutorService extends AbstractExecutorService {
    private final ExecutorService e;

    /**
     * 构造
     *
     * @param executor {@link ExecutorService}
     */
    DelegatedExecutorService( ExecutorService executor ) {
        Precondition.notNull(executor, "executor must be not null !");
        e = executor;
    }

    @Override
    public void execute( Runnable command ) {
        e.execute(command);
    }

    @Override
    public void shutdown() {
        e.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return e.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return e.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return e.isTerminated();
    }

    @Override
    public boolean awaitTermination( long timeout, TimeUnit unit ) throws InterruptedException {
        return e.awaitTermination(timeout, unit);
    }

    @Override
    public Future<?> submit( Runnable task ) {
        return e.submit(task);
    }

    @Override
    public <T> Future<T> submit( Callable<T> task ) {
        return e.submit(task);
    }

    @Override
    public <T> Future<T> submit( Runnable task, T result ) {
        return e.submit(task, result);
    }

    @Override
    public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks ) throws InterruptedException {
        return e.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit )
            throws InterruptedException {
        return e.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny( Collection<? extends Callable<T>> tasks )
            throws InterruptedException, ExecutionException {
        return e.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny( Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit )
            throws InterruptedException, ExecutionException, TimeoutException {
        return e.invokeAny(tasks, timeout, unit);
    }
}
