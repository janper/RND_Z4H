package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PShader;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 08.10.2015.
 */
public class ScnPsycholog implements Scene {
    private PApplet parent;
    private String name = "Psychológ";
    private int bgColour;

    private boolean moving = false;

    private static final int NUM_POINTS = 5;
    public static final int MIN_RADIUS = 200;
    public static final int MAX_RADIUS = 700;

    public static final float MIN_META = 0.05f;
    public static final float MAX_META = 0.15f;

    private ArrayList<PVector> points;
    private ArrayList<PVector> vectors;

    private int mode = 0;

    PShader shader;

    public ScnPsycholog(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        shader = parent.loadShader("metaBall.glsl");

        shader.set("minThreshold", MIN_META);
        shader.set("maxThreshold", MAX_META);
        reset();
        System.out.println(" done!");
    }

    @Override
    public void start() {
        moving = true;
    }

    @Override
    public void stop() {
        moving = false;
        parent.resetShader();
    }

    @Override
    public boolean isPlaying(){
        return moving;
    }

    @Override
    public void display() {
        if  (moving){
            movePoints();
        }
        setPoints();
        parent.pushStyle();
        parent.noStroke();
        parent.beginShape();
        if (mode == 0) {
            parent.fill(255);
        } else {
            parent.fill(255, 0, 0);
        }
        parent.vertex(0, 0);
        if (mode != 0) {
            parent.fill(0, 255, 0);
        }
        parent.vertex(0, parent.height);
        if (mode != 0) {
            parent.fill(0, 0, 255);
        }
        parent.vertex(parent.width, parent.height);
        if (mode != 0) {
            parent.fill(255, 255, 255);
        }
        parent.vertex(parent.width, 0);
        parent.endShape(parent.CLOSE);
        parent.popStyle();
        parent.shader(shader);
    }

    @Override
    public void reset() {
        points = initPoints(NUM_POINTS);
        vectors = initVectors(NUM_POINTS);
    }

    @Override
    public void shuffle() {
        points = initPoints(NUM_POINTS);
    }

    @Override
    public void jitter() {
        vectors = initVectors(NUM_POINTS);
    }

    @Override
    public void mode(int which) {
        mode = which;

    }

    @Override
    public String getName() {
        return name;

    }

    @Override
    public void setBGColour(int colour){
        bgColour = colour;
    }


    private void setPoints() {
        for (int i=0; i<points.size(); i++){
            shader.set("pt0" + i, points.get(i));
        }
        for (int i=0; i<points.size(); i++){
            shader.set("pt0"+(i+points.size()), new PVector(parent.width-points.get(i).x, points.get(i).y, points.get(i).z));
        }
    }

    private ArrayList<PVector> initPoints(int num){
        ArrayList<PVector> tempPoints = new ArrayList<PVector>();
        for (int i = 0; i<num; i++){
            tempPoints.add(new PVector(parent.random(parent.width), parent.random(parent.height), parent.random(MIN_RADIUS, MAX_RADIUS)));
        }
//        System.out.println(tempPoints.toString());

//        System.out.println("new points = " + tempPoints.toString());
        return tempPoints;
    }


    private ArrayList<PVector> initVectors(int num){
        ArrayList<PVector> tempPoints = new ArrayList<PVector>();
        for (int i = 0; i<num; i++){
            tempPoints.add(new PVector(parent.random(-1, 1), parent.random(-1, 1), parent.random(MIN_RADIUS, MAX_RADIUS)));
        }
//        System.out.println("new vectors = " + tempPoints.toString());
        return tempPoints;
    }

    public void movePoints(){
        for (int i=0; i<points.size(); i++) {
            points.get(i).add(new PVector(vectors.get(i).x, vectors.get(i).y, 0));
            if (points.get(i).x<0){
                points.get(i).x=0;
                vectors.get(i).x*=-1;
            }
            if (points.get(i).x>parent.width){
                points.get(i).x=parent.width;
                vectors.get(i).x*=-1;
            }
            if (points.get(i).y<0){
                points.get(i).y=0;
                vectors.get(i).y*=-1;
            }
            if (points.get(i).y>parent.height){
                points.get(i).y=parent.height;
                vectors.get(i).y*=-1;
            }
            points.get(i).z += (vectors.get(i).z-points.get(i).z)*0.01f;
            if ((points.get(i).z-vectors.get(i).z)<0.01f){
                vectors.get(i).z = parent.random(MIN_RADIUS, MAX_RADIUS);
//                System.out.println ("new radius = "+vectors.get(i).z);
            }
        }
    }



}
