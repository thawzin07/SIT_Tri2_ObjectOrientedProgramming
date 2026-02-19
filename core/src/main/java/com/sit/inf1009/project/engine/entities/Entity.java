package com.sit.inf1009.project.engine.entities;

import com.badlogic.gdx.graphics.Texture;
import com.sit.project.engine.render.RenderComponent;

public abstract class Entity {
	private double xPosition;
	private double yPosition;
	private double rotation;
	private boolean visible;
	private Texture texture;
	private RenderComponent renderer;
	private final int id;
	
	public Entity() {
		this.id = 0;
	}
	
	// constructor, id is set here permanently
	protected Entity(int id) {
		this.id = id;
		this.xPosition = 0;
		this.yPosition = 0;
		this.rotation = 0;
		this.visible = true;
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
	public int getID() {
		return id;
	}
	
	// renderer methods
	public void setRenderer(RenderComponent renderer) {
        this.renderer = renderer;
    }
    public RenderComponent getRenderer() {
        return renderer;
    }
}
