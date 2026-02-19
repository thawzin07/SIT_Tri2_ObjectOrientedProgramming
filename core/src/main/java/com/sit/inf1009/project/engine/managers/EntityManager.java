package com.sit.inf1009.project.engine.managers;

import java.util.ArrayList;

import com.sit.inf1009.project.engine.entities.Entity;

public class EntityManager {
	private final ArrayList<Entity> entityList = new ArrayList<Entity>();
	private int nextId = 1;
	
	public int allocateId() {
		return nextId++;
	}
	
	public void addEntity(Entity e) {
		entityList.add(e);
	}
	
	public void removeEntity(Entity e) {
		entityList.remove(e);
	}
	
	public Iterable<Entity> getEntities() {
		return entityList;
	}
	
}
