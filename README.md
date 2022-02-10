# EntityLocker

### Class SimpleExample contains example of usage EntityLocker
###### SimpleExample runs in tests

- Method `handleEntities()` gets list of entities' ids, and increments field `value` in relative entity.
- Entities' ids can repeat


### Tests
- Test `test_RepeatWithDifferentCount` runs method handleEntities many times with different batches of entities
- Test `testDeadLock` run DeadlockExample, but EntityLocker can't avoid deadlock.