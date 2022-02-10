package org.example.model;

public class CustomEntity<E> {
    E id;
    Long value = 0L;

    public CustomEntity(E id) {
        this.id = id;
    }

    public E getId() {
        return id;
    }

    public Long getValue() {
        return value;
    }

    public void increment() {
        value++;
    }
}
