package com.sit.inf1009.project.engine.interfaces;

public interface MovementInterface {
    void update(double dt);

    void setVelocity(double vx, double vy);
    double getVelocityX();
    double getVelocityY();
}
