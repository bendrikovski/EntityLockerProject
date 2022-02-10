package org.example;

import org.example.application.DeadlockExample;
import org.example.application.SimpleExample;
import org.example.model.CustomEntity;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class AppTest {

    @Test
    public void test_smallCount() {
        int start = 0, end = 150, count = 15;

        SimpleExample simpleExample = new SimpleExample();
        checkEntityLock(simpleExample, start, end, count);
        simpleExample.shutdown();
    }

    @Test
    public void test_BigCount() {
        int start = 0, end = 100, count = 15000;

        SimpleExample simpleExample = new SimpleExample();
        checkEntityLock(simpleExample, start, end, count);
        simpleExample.shutdown();
    }

    @Test
    public void test_RepeatWithDifferentCount() {
        int start = 0, end = 10, count = 1000;
        SimpleExample simpleExample = new SimpleExample();

        for (int i = 1; i < count; i++) {
            checkEntityLock(simpleExample, start, end, i);
        }

        simpleExample.shutdown();
    }

//    @Test
    public void testDeadLock() { // Can't avoid such deadlock
        CustomEntity<Integer> entity1 = new CustomEntity<>(1);
        CustomEntity<Integer> entity2 = new CustomEntity<>(2);
        ConcurrentHashMap<Integer, CustomEntity<Integer>> entityMap = new ConcurrentHashMap<>();
        entityMap.put(entity1.getId(), entity1);
        entityMap.put(entity1.getId(), entity2);

        new DeadlockExample().handleDeadLock(entity1, entity2, entityMap);

        assertEquals(2L, entity1.getValue().longValue());
        assertEquals(2L, entity2.getValue().longValue());
    }

    private void checkEntityLock(SimpleExample simpleExample, int start, int end, int count) {
        List<Integer> entityIds = new Random().ints(count, start, end).boxed().collect(Collectors.toList());
        ConcurrentMap<Integer, CustomEntity<Integer>> entityMap = new HashSet<>(entityIds).stream()
                .map(CustomEntity::new)
                .collect(Collectors.toConcurrentMap(CustomEntity::getId, entity -> entity));

        simpleExample.handleEntities(entityIds, entityMap);

        Map<Integer, Long> countMap = entityIds.stream().collect(Collectors.groupingBy(k -> k, Collectors.counting()));

        countMap.forEach((id, value) -> assertEquals(value, entityMap.get(id).getValue()));
        assertEquals(count, entityMap.values().stream().mapToLong(CustomEntity::getValue).sum());
    }
}
