package sample;

import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static sample.Controller.sectionX;

public class Worm extends Thread{

    private Color wormColor;
    List<Rectangle> wormPath;
    int wormLength;
    List<Rectangle> wormBody = new ArrayList<>();
    int wormHeadCoord;
    CollisionSector entry;
    CollisionSector end;
    private Semaphore wholeSectionSemaphore;
    volatile Slider slider;
    int sleepShift;
    @Override
    public void run(){
        while(true){
           moveWorm();
        }
    }

    public Worm(List<Rectangle> wormPath, Color color, int wormLength, CollisionSector entry, CollisionSector end, Semaphore wholeSectionSemaphore, Slider slider, int sleepShift){
        this.wormColor = color;
        this.wormPath = wormPath;
        this.wormLength = wormLength;
        this.entry = entry;
        this.end = end;
        this.sleepShift = sleepShift;
        this. wholeSectionSemaphore=wholeSectionSemaphore;
        this.slider = slider;
        setWorm();
    }

    private void setWorm(){
        for (int i = 0; i < this.wormPath.size(); i++) {
            if (this.wormPath.get(i) == this.entry.getCollisionGrid()) {
                if (i == 0) {
                    this.wormHeadCoord = this.wormPath.size() - 1;
                    break;
                }
                this.wormHeadCoord = i - 1;
                break;
            }
        }
        int bodyCounter = wormHeadCoord - wormLength + 1;

        if (bodyCounter < 0){
            bodyCounter = this.wormPath.size() + bodyCounter;
        }
        while (this.wormPath.get(bodyCounter) != entry.getCollisionGrid()) {
            Rectangle rectangleToAdd = this.wormPath.get(bodyCounter);
            rectangleToAdd.setFill(wormColor);
            wormBody.add(rectangleToAdd);
            bodyCounter = (bodyCounter + 1) % this.wormPath.size();
        }
    }

    private void moveWorm(){
        wormHeadCoord = ((1+wormHeadCoord) % wormPath.size());
        Rectangle nextOnPath = wormPath.get(wormHeadCoord);

        if (nextOnPath == entry.getCollisionGrid()) {
            wholeSectionSemaphore.acquireUninterruptibly();
            entry.getSemaphore().acquireUninterruptibly();
        }
        if (nextOnPath == sectionX.getCollisionGrid()) {
            end.getSemaphore().acquireUninterruptibly();
            sectionX.getSemaphore().acquireUninterruptibly();
        }

        wormBody.add(wormBody.size(), nextOnPath);
        nextOnPath.setFill(wormColor);
        wormBody.get(0).setFill(Color.WHITE);
        Rectangle rectTemp = wormBody.get(0);
        wormBody.remove(0);

        if (rectTemp == sectionX.getCollisionGrid()) {
            entry.getSemaphore().release(1);
            sectionX.getSemaphore().release(1);

        }
        if (rectTemp == end.getCollisionGrid()) {
            end.getSemaphore().release();
            wholeSectionSemaphore.release();
        }


        try{
            long max = new Double(this.slider.getMax()).longValue();
            long value = new Double(this.slider.getValue()).longValue();
            System.out.println( max - value + 100);
            Thread.sleep(4800 - value - sleepShift);

        } catch (Exception e){
            System.out.println("Sleep Error while moving worm");
        }
    }

}

