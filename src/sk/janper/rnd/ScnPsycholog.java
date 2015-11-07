package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;

import java.util.ArrayList;

/**
 * Created by Jan on 08.10.2015.
 */
public class ScnPsycholog implements Scene {
    public static final int MIN_RADIUS = 200;
    public static final int MAX_RADIUS = 500;
    public static final float MIN_META = 0.05f;
    public static final float MAX_META = 0.35f;
    private static final int NUM_POINTS = 5;
    PShader shader;
    private PApplet parent;
    private String name = "Psycholog";
    private int bgColour;
    private boolean moving = false;
    private ArrayList<PVector> points;
    private ArrayList<PVector> vectors;
    private int mode = 0;

    private BufferShader bufferShader;
    private int counter = 0;

    private int NUM_FILLS = 2;

    private int[][] fills = new int[NUM_FILLS][4];
    private int currentFill = 0;

    private boolean direct = true;
    private float stepSpeed;

    private float speedMultiplier = 0.25f;

    public ScnPsycholog(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        shader = parent.loadShader("metaBall.glsl");

        shader.set("minThreshold", MIN_META);
        shader.set("maxThreshold", MAX_META);

        bufferShader = new BuffPsycholog(parent);
        setFills();

        reset();
        System.out.println(" done!");
    }

    private void setFills(){
        fills [0][0] = parent.color(255);
        fills [0][1] = parent.color(255);
        fills [0][2] = parent.color(255);
        fills [0][3] = parent.color(255);

        fills [1][0] = parent.color(12, 52, 173);
        fills [1][1] = parent.color(96, 113, 163);
        fills [1][2] = parent.color(232, 121, 2);
        fills [1][3] = parent.color(232, 18, 2);
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
    public void display(PGraphics buffer) {
        if  (moving){
            movePoints();
            counter++;
        }
        setPoints();

        buffer.beginDraw();
        buffer.clear();
        buffer.shader(shader);
        buffer.pushStyle();
        buffer.noStroke();
        buffer.beginShape();
        buffer.fill(fills[currentFill%NUM_FILLS][0]);
        buffer.vertex(0, 0);
        buffer.fill(fills[currentFill%NUM_FILLS][1]);
        buffer.vertex(0, parent.height);
        buffer.fill(fills[currentFill%NUM_FILLS][2]);
        buffer.vertex(parent.width, parent.height);
        buffer.fill(fills[currentFill%NUM_FILLS][3]);
        buffer.vertex(parent.width, 0);
        buffer.endShape(PConstants.CLOSE);
        buffer.popStyle();
        buffer.resetShader();
        buffer.endDraw();
    }

    @Override
    public void reset() {
        points = initPoints(NUM_POINTS);
        vectors = initVectors(NUM_POINTS);
        counter=0;
        mode = 0;
        stepSpeed = 0.01f;
    }

    @Override
    public void shuffle() {
        vectors.forEach(v -> {
            v.x =parent.random(-1, 1);
            v.y=parent.random(-1, 1);
            v.z = parent.random(MIN_RADIUS, MAX_RADIUS);
        });
    }

    @Override
    public void jitter() {
        if (mode!=9) {
            randomizeRadii();
        }
    }

    private void randomizeRadii() {
        vectors.forEach(v -> v.z = parent.random(MIN_RADIUS, MAX_RADIUS));
    }

    @Override
    public void mode(int which) {
        if (mode==9 && which!=9){
            randomizeRadii();
        }
        mode = which;
        if (mode==0){
            currentFill = 0;
        }
        if (mode==1){
            currentFill = 1;
        }
        if (mode==9){
            vectors.forEach(v -> v.z = 0f);
            stepSpeed = 0.1f;
        }
        if (mode!=9){
            stepSpeed = 0.01f;
        }
        if (mode==2){
            speedMultiplier = 0.25f;
        }
        if (mode==3){
            speedMultiplier = 1f;
        }

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
            shader.set("pt0"+(i+points.size()), new PVector(parent.width-points.get(i).x, points.get(i).y, points.get(i).z));
        }
    }

    private ArrayList<PVector> initPoints(int num){
        ArrayList<PVector> tempPoints = new ArrayList<PVector>();
        for (int i = 0; i<num; i++){
            tempPoints.add(new PVector(parent.random(parent.width), parent.random(parent.height), 0f));
        }
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
            points.get(i).add(new PVector(vectors.get(i).x*speedMultiplier, vectors.get(i).y*speedMultiplier, 0));
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
            points.get(i).z += (vectors.get(i).z-points.get(i).z)* stepSpeed;
            float difference = Math.abs(points.get(i).z - vectors.get(i).z);
            if (difference <0.01f && mode!=9){
                vectors.get(i).z = parent.random(MIN_RADIUS, MAX_RADIUS);
            }
        }
    }

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public PShader getShader() {
        return bufferShader.getShader(counter);
    }

    @Override
    public void display() {
        if  (moving){
            movePoints();
            counter++;
        }
        setPoints();
        
        parent.shader(shader);
        parent.pushStyle();
        parent.noStroke();
        parent.beginShape();
        parent.fill(fills[currentFill%NUM_FILLS][0]);
        parent.vertex(0, 0);
        parent.fill(fills[currentFill%NUM_FILLS][1]);
        parent.vertex(0, parent.height);
        parent.fill(fills[currentFill%NUM_FILLS][2]);
        parent.vertex(parent.width, parent.height);
        parent.fill(fills[currentFill%NUM_FILLS][3]);
        parent.vertex(parent.width, 0);
        parent.endShape(PConstants.CLOSE);
        parent.popStyle();
        parent.resetShader();
    }

    @Override
    public boolean isDirect() {
        return direct;
    }
}
