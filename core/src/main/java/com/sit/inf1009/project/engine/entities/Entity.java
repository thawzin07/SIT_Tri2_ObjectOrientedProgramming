package com.sit.inf1009.project.engine.entities;

import com.badlogic.gdx.graphics.Texture;

public abstract class Entity {
	private double xPosition;
	private double yPosition;
	private double rotation;
	private boolean visible;
	private Texture texture;
	private int id;
	
	public Entity() {
	}
	
	public Entity(double x, double y, double rotate, boolean visible) {
		this.xPosition = x;
		this.yPosition = y;
		this.rotation = rotate;
		this.visible = visible;
	}

	// xPosition Methods
	public void setX(double x) {
		this.xPosition = x;
	}

	public double getX() {
		return xPosition;
	}
	
	// yPosition Methods
	public void setY(double y) {
		this.yPosition = y;
	}
	
	public double getY() {
		return yPosition;
	}
	
	
	// rotation Methods
	public void setRotation(double rotate) {
		this.rotation = rotate;
	}
	
	public double getRotation() {
		return rotation;
	}
	
	// visible Methods
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean getVisible() {
		return visible;
	}
	
	// texture Methods
	public void setTexture(Texture tex) {
		this.texture = tex;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	// id Methods
	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return this.id;
	}
}
