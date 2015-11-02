package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

import java.util.ArrayList;

/**
 * Created by rndzvuk on 2.11.2015.
 */
public class ScnStavebniny implements Scene {
    private PApplet parent;

    private String name = "Stavebniny";
    private int bgColour;
    private boolean moving = false;
    private int mode = 0;

    private FlakeBuffer fallen;

    private ArrayList<Flake> flakes = new ArrayList<>();

    private int counter = 0;


    public ScnStavebniny(PApplet parent) {
        this.parent = parent;
        System.out.print("Constructing "+name);
        this.parent = parent;
        fallen = new FlakeBuffer(parent.width, parent.height, 0.25f);
        reset();
        System.out.println(" done!");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() {
        moving = true;
    }

    @Override
    public void stop() {
        moving=false;
    }

    @Override
    public void display(PGraphics buffer) {
        if (moving){
            if  (counter%10==0) {
                flakes.add(new Flake(parent, parent.random(parent.width), 0, fallen));
            }
            ArrayList<Flake> newFlakes = new ArrayList<>();
            flakes.forEach(f -> {
                f.update();
                if (!f.isSettled()){
                    newFlakes.add(f);
                }
            });
            flakes = newFlakes;
            counter++;
        }

        buffer.beginDraw();
        buffer.clear();
        buffer.stroke (parent.color(255));
        buffer.strokeWeight (4f);
        flakes.forEach(f -> f.display(buffer));
        buffer.endDraw();

        fallen.display(parent,buffer);
    }

    @Override
    public void reset() {
        fallen.reset();
        counter = 0;
    }

    @Override
    public void shuffle() {
        flakes.forEach(f -> f.getFallDirection().jitter (1f));
    }

    @Override
    public void jitter() {
        flakes.forEach(f -> f.jitter(1f));
    }

    @Override
    public void mode(int which) {
        mode = which;

    }

    @Override
    public void setBGColour(int colour) {
        bgColour = colour;

    }

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public boolean isPlaying() {
        return moving;
    }

    @Override
    public PShader getShader() {
        return null;
    }
}
