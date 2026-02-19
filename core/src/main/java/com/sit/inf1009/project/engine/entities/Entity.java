package com.sit.inf1009.project.engine.entities;
import com.badlogic.gdx.graphics.Texture;
import com.sit.inf1009.project.engine.components.MovementComponent;
import com.sit.inf1009.project.engine.components.CollidableComponent;

public class Entity {
    private double xPosition;
    private double yPosition;
    private double rotation;
    private boolean visible;
    private Texture texture;
    private int id;
    private MovementComponent movement;
    private CollidableComponent collidable;

    public Entity(int id) {
        this();          // call the default constructor so you don’t repeat code
        this.id = id;
    }
    
    public Entity() {
        this.xPosition = 0;
        this.yPosition = 0;
        this.rotation = 0;
        this.visible = true;
    }

    // Position
    public double getXPosition() { return xPosition; }
    public double getYPosition() { return yPosition; }
    public void setXPosition(double x) { this.xPosition = x; }
    public void setYPosition(double y) { this.yPosition = y; }

    // Rotation
    public void setRotation(double rotate) { this.rotation = rotate; }
    public double getRotation() { return rotation; }

    // Visible
    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean getVisible() { return visible; }

    // Texture
    public void setTexture(Texture tex) { this.texture = tex; }
    public Texture getTexture() { return texture; }

    // ID
    public void setID(int id) { this.id = id; }
    public int getID() { return id; }

    // Movement
    public void setMovement(MovementComponent movement) { this.movement = movement; }
    public MovementComponent getMovement() { return movement; }
    
    // Collisions
    public void setCollidable(CollidableComponent collidable) { this.collidable = collidable; }
    public CollidableComponent getCollidable() { return collidable; }

    public void update(double dt) {
        if (movement != null) movement.update(this, dt);
    }
}
