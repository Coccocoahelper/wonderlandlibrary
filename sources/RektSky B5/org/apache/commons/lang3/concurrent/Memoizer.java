/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.apache.commons.lang3.concurrent.Computable;

public class Memoizer<I, O>
implements Computable<I, O> {
    private final ConcurrentMap<I, Future<O>> cache = new ConcurrentHashMap<I, Future<O>>();
    private final Computable<I, O> computable;
    private final boolean recalculate;

    public Memoizer(Computable<I, O> computable) {
        this(computable, false);
    }

    public Memoizer(Computable<I, O> computable, boolean recalculate) {
        this.computable = computable;
        this.recalculate = recalculate;
    }

    @Override
    public O compute(final I arg) throws InterruptedException {
        while (true) {
            Callable eval;
            FutureTask futureTask;
            FutureTask future;
            if ((future = (FutureTask)this.cache.get(arg)) == null && (future = (Future)this.cache.putIfAbsent(arg, futureTask = new FutureTask(eval = new Callable<O>(){

                @Override
                public O call() throws InterruptedException {
                    return Memoizer.this.computable.compute(arg);
                }
            }))) == null) {
                future = futureTask;
                futureTask.run();
            }
            try {
                return (O)future.get();
            }
            catch (CancellationException e2) {
                this.cache.remove(arg, future);
                continue;
            }
            catch (ExecutionException e3) {
                if (this.recalculate) {
                    this.cache.remove(arg, future);
                }
                throw this.launderException(e3.getCause());
            }
            break;
        }
    }

    private RuntimeException launderException(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException)throwable;
        }
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        throw new IllegalStateException("Unchecked exception", throwable);
    }
}

