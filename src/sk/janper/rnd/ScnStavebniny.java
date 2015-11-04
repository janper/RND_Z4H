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

    private PGraphics obstacles;
    private PGraphics flakesBuffer;
    private PGraphics tempFlakes;

    private PShader flakesShader;
    private PShader obstaclesShader;

    private ArrayList<Flake> flakes = new ArrayList<>();

    private int counter = 0;

    private boolean direct = false;


    public ScnStavebniny(PApplet parent) {
        this.parent = parent;
        System.out.print("Constructing "+name);
        this.parent = parent;
        fallen = new FlakeBuffer(parent.width, parent.height, 0.25f);
        reset();

        flakesShader = parent.loadShader("flakesShader.glsl");
        obstaclesShader = parent.loadShader("obstaclesShader.glsl");

        obstacles = parent.createGraphics(parent.width, parent.height, PApplet.P2D);
        obstacles.beginDraw();
        obstacles.stroke(parent.color(255));
        obstacles.strokeWeight(4f);
        obstacles.line(0,0,0,parent.height-1);
        obstacles.line(parent.width-1,0,parent.width-1,parent.height-1);
        obstacles.line(0,parent.height-100,parent.width-1,parent.height-100);
        obstacles.endDraw();

        flakesBuffer = parent.createGraphics(parent.width, parent.height, PApplet.P2D);
        flakesBuffer.beginDraw();
        flakesBuffer.stroke(parent.color(255,0,0));
        flakesBuffer.strokeWeight(10f);
//        for (int i=0; i<3; i++){
//            flakesBuffer.point (parent.random(flakesBuffer.width), parent.random(flakesBuffer.height));
//        }

        flakesBuffer.point (flakesBuffer.width/2, flakesBuffer.height/2);
//        flakesBuffer.shader(flakesShader);
        flakesBuffer.endDraw();

        tempFlakes = parent.createGraphics(parent.width, parent.height, PApplet.P2D);
        tempFlakes.shader(flakesShader);


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
//            if  (counter%4==0) {
//                flakes.add(new Flake(parent, parent.random(parent.width), 0, obstacles));
//            }
//            ArrayList<Flake> newFlakes = new ArrayList<>();
//            flakes.forEach(f -> {
//                f.update();
//                if (!f.isSettled()){
//                    newFlakes.add(f);
//                }
//            });
//            flakes = newFlakes;

            flakesShader.set("obstacles", obstacles.get());

            buffer.beginDraw();
            buffer.clear();

            tempFlakes.beginDraw();
            tempFlakes.clear();
            tempFlakes.image(flakesBuffer, 0,0);
            tempFlakes.endDraw();

            flakesBuffer = tempFlakes;
//            flakesBuffer.image(flakesBuffer,0,0);

//            flakesBuffer.beginDraw();
//            flakesBuffer.clear();
//            flakesBuffer.image(tempFlakes,0,0);
//            flakesBuffer.endDraw();

            buffer.image (flakesBuffer.get(), 0,0);

            buffer.image(obstacles.get(),0,0);

            buffer.endDraw();

//            parent.noLoop();

            counter++;
        }
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

    @Override
    public void display() {

    }

    @Override
    public boolean isDirect() {
        return false;
    }
}
