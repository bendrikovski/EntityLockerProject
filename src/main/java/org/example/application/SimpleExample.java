package org.example.application;

import org.example.model.CustomEntity;
import org.example.model.EntityLockerImpl;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SimpleExample {
    ExecutorService executorService = Executors.newCachedThreadPool();

    public void handleEntities(List<Integer> entityIds, ConcurrentMap<Integer, CustomEntity<Integer>> entityMap) {
        EntityLockerImpl<Integer> entityLocker = new EntityLockerImpl<>();
        Consumer<Integer> consumer = entityId -> {
            try {
                entityLocker.lock(entityId);
                entityMap.get(entityId).increment();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                entityLocker.unlock(entityId);
            }
        };

        List<Future<?>> futures = entityIds.stream().map(entityId -> executorService.submit(() -> consumer.accept(entityId))).collect(Collectors.toList());
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

}
