package com.sit.inf1009.project.engine.managers;

import java.util.ArrayList;
import java.util.List;

import com.sit.inf1009.project.engine.interfaces.MovementInterface;

// Commment by TZ 
// You can implement something like this 
// 
//public class MovementManager implements IOListener {
//
//    @Override
//    public void onIOEvent(IOEvent event) {
//        if (event.getType().equals("KEY_PRESSED")) {
//
//            switch (event.getKeyCode()) {
//                case "W":
//                    moveUp();
//                    break;
//                case "S":
//                    moveDown();
//                    break;
//                case "A":
//                    moveLeft();
//                    break;
//                case "D":
//                    moveRight();
//                    break;
//            }
//        }
//    }
//
//    private void moveUp() {
//        // movement logic here
//    }
//
//    private void moveDown() { }
//
//    private void moveLeft() { }
//
//    private void moveRight() { }
//}


public class MovementManager {

    private final List<MovementInterface> movableEntities = new ArrayList<>();

    public void add(MovementInterface entity) {
        movableEntities.add(entity);
    }

    public void remove(MovementInterface entity) {
        movableEntities.remove(entity);
    }

    public void updateAll(double dt) {
        for (MovementInterface entity : movableEntities) {
            entity.update(dt);
        }
    }

    public void clear() {
        movableEntities.clear();
    }
}
