package com.whaleal.icefrog.core.thread.lock;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 无锁实现
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class NoLock implements Lock , Serializable {

    @Override
    public void lock() {
    }

    @Override
    public void lockInterruptibly() {
    }

    @Override
    public boolean tryLock() {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean tryLock( long time, TimeUnit unit ) {
        return true;
    }

    @Override
    public void unlock() {
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("NoLock`s newCondition method is unsupported");
    }

}
