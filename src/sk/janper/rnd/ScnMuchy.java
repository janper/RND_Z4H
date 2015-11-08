package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 06.10.2015.
 */
public class ScnMuchy implements Scene {
    public ArrayList<Fly> flies;
    public int numFlies = 50;
    public String name = "Muchy";
    private PApplet parent;
    private boolean move = false;
    private int bgColour;

    private boolean direct = true;

    private int mode = 0;

    public ScnMuchy(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        reset();
        System.out.println(" done!");
    }

    public void reset(){
        flies= new ArrayList<>();
        initFlies();
    }

    public void display (PGraphics buffer){
        buffer.beginDraw();
        buffer.clear();
        if (move) {
            flies.forEach(f -> f.update());
        }
        flies.forEach(f -> f.display(buffer));
        buffer.endDraw();
    }

    @Override
    public void display() {
        if (move) {
            if (mode==9){
                flies.forEach(f -> f.leave());
            } else {
                flies.forEach(f -> f.update());
            }
        }
        flies.forEach(f -> f.display());
    }

    public void shuffle(){

    }

    public void jitter(){

    }

    public void start(){
        move = true;
    }

    public void stop(){
        move = false;
    }

    public void mode(int which){
        mode = which;
    }

    public boolean isPlaying(){
        return move;
    }

    public String getName(){
        return name;
    }

    public void setBGColour(int colour){
        bgColour = colour;
    }

    public void initFlies(){
        for (int i=0 ; i<numFlies; i++) {
            Fly f = new Fly(parent, new Vec3D(parent.random (-parent.width/2, -50), parent.random (-parent.height/2, -50), 0f));
            flies.add(f);
        }
    }

    @Override
    public int getCounter() {
        return 0;
    }

    @Override
    public PShader getShader() {
        return null;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }
}
