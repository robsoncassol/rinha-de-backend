package com.cassol.rinhadebackend.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TransactionTaskRunnerImpl implements TransactionTaskRunner{

    private final TransactionTemplate readWriteTransactionTemplate;
    private final TransactionTemplate readOnlyTransactionTemplate;
    private final TransactionTemplate newReadWriteTransactionTemplate;

    public void onCommit(Runnable task) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            public void afterCommit() {
                task.run();
            }
        });
    }

    public void readWrite(Runnable task) {
        readWriteTransactionTemplate.execute(status -> {
            task.run();
            return null;
        });
    }

    private void newReadWrite(Runnable task) {
        newReadWriteTransactionTemplate.execute(status -> {
            task.run();
            return null;
        });
    }

    public <T> T readWriteAndGet(Supplier<T> task) {
        return readWriteTransactionTemplate.execute(status -> task.get());
    }

    public void readOnly(Runnable task) {
        readOnlyTransactionTemplate.execute(status -> {
            task.run();
            return null;
        });
    }

    public <T> T readOnlyAndGet(Supplier<T> task) {
        return readOnlyTransactionTemplate.execute(status -> task.get());
    }

    @Override
    public void forceReadWriteOnCommit(Runnable task) {
        /**
         * {@link #forceReadWriteOnCommit(Runnable)} {@link TransactionSynchronization}
         */
        class ForceReadWriteSynchronization extends TransactionSynchronizationAdapter {
            private boolean ran = false;
            private List<Runnable> tasks = new LinkedList<>();

            @Override
            public void beforeCommit(boolean readOnly) {
                if (!readOnly) {
                    run();
                }
            }

            @Override
            public void afterCommit() {
                if (!ran) {
                    newReadWrite(this::run);
                }
            }

            public void addTask(Runnable task) {
                tasks.add(task);
            }

            private void run() {
                tasks.forEach(Runnable::run);
                ran = true;
            }
        }
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            readWrite(task);
        } else if (!TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            task.run();
        } else {
            TransactionSynchronizationManager.getSynchronizations().stream()
                .filter(ForceReadWriteSynchronization.class::isInstance)
                .map(ForceReadWriteSynchronization.class::cast)
                .findFirst()
                .orElseGet(() -> {
                    ForceReadWriteSynchronization synchronization = new ForceReadWriteSynchronization();
                    TransactionSynchronizationManager.registerSynchronization(synchronization);
                    return synchronization;
                })
                .addTask(task);
        }
    }

}
