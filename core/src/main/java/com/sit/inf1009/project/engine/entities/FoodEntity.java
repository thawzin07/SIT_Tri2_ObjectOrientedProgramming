package com.sit.inf1009.project.engine.entities;

import com.badlogic.gdx.graphics.Color;
import com.sit.inf1009.project.engine.interfaces.FoodCategory;

public final class FoodEntity extends Entity {
	private final FoodCategory type;
    private final Color color;

    public FoodEntity(int id, FoodCategory type, Color color) {
        super(id);
        this.type = type;
        this.color = color;
    }

    public FoodCategory getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }
}
