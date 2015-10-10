package sk.janper.rnd;

import processing.core.PApplet;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 06.10.2015.
 */
public class ScnMuchy implements Scene {
    private PApplet parent;

    public ArrayList<Fly> flies;
    public int numFlies = 100;

    public String name = "Muchy";

    private boolean move = false;
    private int bgColour;

    public ScnMuchy(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        reset();
        System.out.println("done!");
    }

    public void reset(){
        flies= new ArrayList<Fly>();
        initFlies();
    }

    public void display (){
        if (move) {
            flies.forEach(f -> f.update());
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
    }

    public String getName(){
        return name;
    }

    public void setBGColour(int colour){
        bgColour = colour;
    }

    public void initFlies(){
        for (int i=0 ; i<numFlies; i++) {
            Fly f = new Fly(parent, new Vec3D());
            flies.add(f);
        }
    }


}
