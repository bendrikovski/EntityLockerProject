package org.example.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class EntityLockerImpl<E> implements EntityLocker<E> {
    ConcurrentHashMap<E, ReentrantLock> map;

    public EntityLockerImpl() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public void lock(E entityId) throws InterruptedException {
        ReentrantLock lock = map.computeIfAbsent(entityId, x -> new ReentrantLock());
        lock.lock();
    }

    @Override
    public void unlock(E entityId) {
        ReentrantLock lock = map.get(entityId);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @Override
    public boolean tryLock(E entityId, long timeoutMillis) throws InterruptedException {
        ReentrantLock lock = map.computeIfAbsent(entityId, x -> new ReentrantLock());
        return lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS);
    }
}