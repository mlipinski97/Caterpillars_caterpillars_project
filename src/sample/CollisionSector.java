package sample;

import javafx.scene.shape.Rectangle;
import java.util.concurrent.Semaphore;

public class CollisionSector {

    private Rectangle collisionGrid;
    private Semaphore semaphore;

    public CollisionSector(Rectangle collisionGrid){
        this.collisionGrid = collisionGrid;
        semaphore = new Semaphore(1);
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public Rectangle getCollisionGrid(){
        return collisionGrid;
    }
}
