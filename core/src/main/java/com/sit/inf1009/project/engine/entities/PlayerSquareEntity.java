package com.sit.inf1009.project.engine.entities;

public class PlayerSquareEntity extends Entity {
    private final float size; // side length

    public PlayerSquareEntity(int id, double x, double y, float size) {
        super(id);
        setXPosition(x);
        setYPosition(y);
        this.size = size;
    }

    public float getSize() {
        return size;
    }
}
