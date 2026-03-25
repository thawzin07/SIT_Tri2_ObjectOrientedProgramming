package com.sit.inf1009.project.game.entities;

import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.game.domain.FoodCategory;

public final class FoodEntity extends Entity {
	private final FoodCategory type;

    public FoodEntity(int id, FoodCategory type) {
        super(id);
        this.type = type;
    }

    public FoodCategory getType() {
        return type;
    }
}
