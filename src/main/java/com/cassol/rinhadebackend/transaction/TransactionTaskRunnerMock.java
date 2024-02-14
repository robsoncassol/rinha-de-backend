package com.cassol.rinhadebackend.transaction;


import java.util.function.Supplier;

public class TransactionTaskRunnerMock implements TransactionTaskRunner {
    public void onCommit(Runnable task) {
        task.run();
    }

    public void readWrite(Runnable task) {
        task.run();
    }

    public <T> T readWriteAndGet(Supplier<T> task) {
        return task.get();
    }

    public void readOnly(Runnable task) {
        task.run();
    }

    public <T> T readOnlyAndGet(Supplier<T> task) {
        return task.get();
    }

    @Override
    public void forceReadWriteOnCommit(Runnable task) {
        task.run();
    }

}
