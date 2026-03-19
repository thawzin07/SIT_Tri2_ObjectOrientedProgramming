package com.sit.inf1009.project.engine.entities;

public class PlateEntity extends Entity {
    public PlateEntity(int id, double x, double y) {
        super(id);
        setXPosition(x);
        setYPosition(y);
    }
}
