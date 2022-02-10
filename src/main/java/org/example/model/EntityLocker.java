package org.example.model;

public interface EntityLocker<E> {
    void lock(E entityId) throws InterruptedException;

    void unlock(E entityId);

    boolean tryLock(E entityId, long timeoutMillis) throws InterruptedException;
}
