package com.sit.inf1009.project.engine.managers;

import java.util.ArrayList;

import com.sit.inf1009.project.engine.entities.Entity;

public class EntityManager {
	private ArrayList<Entity> entityList = new ArrayList<Entity>();
	
	public void addEntity(Entity e) {
		entityList.add(e);
	}
	
	public void removeEntity(Entity e) {
		entityList.remove(e);
	}
	
}
