package org.example.application;

import org.example.model.CustomEntity;
import org.example.model.EntityLockerImpl;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class DeadlockExample {
    final int TIMEOUT = 1000;
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    EntityLockerImpl<Integer> entityLocker = new EntityLockerImpl<>();
    Random random = new Random();

    public void handleDeadLock(CustomEntity<Integer> entity1, CustomEntity<Integer> entity2, ConcurrentHashMap<Integer, CustomEntity<Integer>> entityMap) {
        BiConsumer<Integer, Integer> consumer = getConsumer(entityMap);
        executorService.submit(() -> consumer.accept(entity1.getId(), entity2.getId()));
        executorService.submit(() -> consumer.accept(entity1.getId(), entity2.getId()));
        executorService.shutdown();
    }

    public BiConsumer<Integer, Integer> getConsumer(ConcurrentHashMap<Integer, CustomEntity<Integer>> entityMap) {
        return (entityId1, entityId2) -> {
            try {
                while (true) {
                    if (entityLocker.tryLock(entityId1, TIMEOUT)) {
                        try {
                            if (entityLocker.tryLock(entityId2, TIMEOUT)) {
                                try {
                                    entityMap.get(entityId1).increment();
                                    entityMap.get(entityId2).increment();
                                    break;
                                } finally {
                                    entityLocker.unlock(entityId2);
                                }
                            }
                        } finally {
                            entityLocker.unlock(entityId1);
                        }
                    }
                    Thread.sleep(TIMEOUT + random.nextInt(TIMEOUT));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };
    }
}