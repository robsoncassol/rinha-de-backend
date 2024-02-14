package com.cassol.rinhadebackend.transaction;

import java.util.function.Supplier;

public interface TransactionTaskRunner {
    /**
     * Executes the given task after the current transaction commits.
     *
     * @param task The task to be executed.
     */
    void onCommit(Runnable task);

    /**
     * Creates a read/write traansaction and executes the given task in it.
     * If a transaction already exists a new one will not be created.
     * Equivalent to: <code>@Transactional(propagation = REQUIRED)</code>
     *
     * @param task The task to be executed.
     */
    void readWrite(Runnable task);

    /**
     * Creates a read/write traansaction and executes the given task in it returning the output of the task.
     * If a transaction already exists a new one will not be created.
     * Equivalent to: <code>@Transactional(propagation = REQUIRED)</code>
     *
     * @param task The task to be executed.
     */
    <T> T readWriteAndGet(Supplier<T> task);

    /**
     * Creates a read-only traansaction and executes the given task in it.
     * If a transaction already exists a new one will not be created.
     * Equivalent to: <code>@Transactional(propagation = REQUIRED, readOnly = true)</code>
     *
     * @param task The task to be executed.
     */
    void readOnly(Runnable task);

    /**
     * Creates a read-only traansaction and executes the given task in it returning the output of the task.
     * If a transaction already exists a new one will not be created.
     * Equivalent to: <code>@Transactional(propagation = REQUIRED, readOnly = true)</code>
     *
     * @param task The task to be executed.
     */
    <T> T readOnlyAndGet(Supplier<T> task);

    /**
     * Guarantees that the given task is executed in a read/write transaction.
     * The task is executed around the end of lifecycle of the current transaction in the following manner:
     * <ul>
     *   <li>If there is no current transaction, a new read/write transaction is created and the task is executed immediately.</li>
     *   <li>If the current transaction is read/write, the task is run in that transaction right before committing.</li>
     *   <li>If the current transaction is read-only, the task is run in in a new read-write transaction right after committing the read-only one.
     *   If multiple tasks are submitted during a read-only transaction, all of them are run together in the same read-write transaction.</li>
     * </ul>
     *
     * Exceptions in either task or the rest of the transaction code result in no changes at all begin saved to database.
     * @param task The task to be executed.
     */
    void forceReadWriteOnCommit(Runnable task);

}
