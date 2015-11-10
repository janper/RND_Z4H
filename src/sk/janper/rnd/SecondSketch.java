package sk.janper.rnd;

import processing.core.PApplet;

/**
 * Created by rndzvuk on 10.11.2015.
 */
public class SecondSketch extends PApplet {
    private static final int MULTIPLIER = 20;
    public static final int SECOND_WIDTH = 16 * MULTIPLIER;
    public static final int SECOND_HEIGHT = 9 * MULTIPLIER;
    private PApplet mainWindow;

    public void settings(){
        size (SECOND_WIDTH, 9*20, P2D);
        displayDensity(pixelDensity);
    }

    public void setup(){
        stroke(255);
        strokeWeight(2f);
        noFill();
    }

    public void draw(){
        background(0);
        ellipse(width/2, height/2, 100,100);
    }

    public void setMainWindow(PApplet mainWindow) {
        this.mainWindow = mainWindow;
    }
}
