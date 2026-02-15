package com.sit.inf1009.project.engine.core;

public class Vector2 {
    private double x;
    private double y;
    
    // Constructor
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    // Default constructor (0, 0)
    public Vector2() {
        this(0, 0);
    }
    
    // Getters
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    // Setters
    public void setX(double x) {
        this.x = x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    // Add another vector to this one
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }
    
    // Subtract another vector from this one
    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }
    
    // Multiply by a scalar
    public Vector2 multiply(double scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }
    
    // Divide by a scalar
    public Vector2 divide(double scalar) {
        if (scalar == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return new Vector2(this.x / scalar, this.y / scalar);
    }
    
    // Calculate magnitude (length) of vector
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
    
    // Normalize the vector (make it length 1)
    public Vector2 normalize() {
        double mag = magnitude();
        if (mag == 0) {
            return new Vector2(0, 0);
        }
        return new Vector2(x / mag, y / mag);
    }
    
    // Dot product with another vector
    public double dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }
    
    // Distance to another vector
    public double distanceTo(Vector2 other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    // Create a copy of this vector
    public Vector2 copy() {
        return new Vector2(this.x, this.y);
    }
    
    // String representation for debugging
    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
    
    // Check if two vectors are equal
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2 other = (Vector2) obj;
        return Double.compare(other.x, x) == 0 && 
               Double.compare(other.y, y) == 0;
    }
    
    // Static helper methods
    public static Vector2 zero() {
        return new Vector2(0, 0);
    }
    
    public static Vector2 up() {
        return new Vector2(0, -1); // Negative Y is typically "up" in screen coordinates
    }
    
    public static Vector2 down() {
        return new Vector2(0, 1);
    }
    
    public static Vector2 left() {
        return new Vector2(-1, 0);
    }
    
    public static Vector2 right() {
        return new Vector2(1, 0);
    }
}