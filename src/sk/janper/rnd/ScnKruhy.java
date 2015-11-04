package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toxi.geom.Vec2D;

import java.util.ArrayList;

/**
 * Created by Jan on 08.10.2015.
 */

public class ScnKruhy implements Scene {
    private PApplet parent;
    private String name = "Kruhy v obili";
    private int bgColour;

    private boolean moving = false;

    private Gear gear;

    private int depth = 4;
    private ArrayList<Float> originalRadii = new ArrayList<Float>();
    private ArrayList<Float> currentRadii = new ArrayList<Float>();
    private ArrayList<Float> targetRadii = new ArrayList<Float>();

    private ArrayList<Float> originalSpeed = new ArrayList<Float>();
    private ArrayList<Float> currentSpeed = new ArrayList<Float>();
    private ArrayList<Float> targetSpeed = new ArrayList<Float>();

    private ArrayList<Gear> gears = new ArrayList<Gear>();

    private int transitionSteps = 50000;

    private BufferShader bufferShader;

    private int counter;
    private boolean direct = false;


    public ScnKruhy(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        bufferShader = new BuffKruhy(parent);
        reset();
        randomize();
        System.out.println(" done!");
    }

    @Override
    public void start() {
        moving = true;
    }

    @Override
    public void stop() {
        moving =false;
    }

    @Override
    public void display(PGraphics buffer) {
        if (moving){
            counter++;
        }

        mapCurrent();
        makeGears();

        if (bufferShader.isJustAnim(counter)){
            direct = true;
            display();
        } else {
            direct = false;
            buffer.beginDraw();
            buffer.clear();
            displayCurrentSpirograph(1500, buffer);
            buffer.endDraw();
        }
    }

    @Override
    public void display() {
        if (moving){
            counter++;
        }

        mapCurrent();
        makeGears();
        
        displayCurrentSpirograph(1500);

        if (!bufferShader.isJustAnim(counter)){
            direct = false;
        }
    }

    private void displayCurrentSpirograph(int count) {
        ArrayList<Vec2D> points = getPoints(count);
        parent.pushStyle();
        parent.strokeWeight(4f);
        parent.beginShape(PConstants.POINTS);
        parent.noFill();
//        parent.camera(parent.width/2,parent.height/2,parent.height/2, 0,parent.height,0, 0,1,0);

        int fade = 100;
        for (int i=0; i<points.size();i++){
            if (i>=0 && i<=fade){
                parent.stroke(parent.lerpColor(parent.color(255, 0), parent.color(255, 255), PApplet.map(i, 0, fade, 0, 1)));
            } else{
                if (i>=points.size()-fade && i<=points.size()){
                    parent.stroke(parent.lerpColor(parent.color(255, 0), parent.color(255, 255), PApplet.map(i, points.size() - fade, points.size(), 1, 0)));
                }else{
                    parent.stroke(255);
                }
            }
            parent.vertex(points.get(i).x, points.get(i).y);
        }
        parent.endShape();
        parent.popStyle();
    }

    @Override
    public void reset() {
        prepareGears();
        randomizeTarget();
        counter = 0;
    }

    @Override
    public void shuffle() {
        randomize();
    }

    @Override
    public void jitter() {
        randomizeTarget();
    }

    @Override
    public void mode(int which) {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setBGColour(int colour){
        bgColour = colour;
    }

    @Override
    public boolean isPlaying(){
        return moving;
    }

    private void randomizeTarget(){
        float lastRadius = parent.height;
        targetRadii.clear();
        targetSpeed.clear();
        for (int i=0 ; i<this.depth; i++){
            float randomRadius = parent.random(lastRadius / 4, lastRadius / 2);
            targetRadii.add(randomRadius);
            lastRadius = randomRadius;
            targetSpeed.add(parent.random(-PConstants.PI / ((this.depth - i) * 10), PConstants.PI / ((this.depth - i) * 10)));
        }
    }

    private void setOriginal(){
        originalRadii.clear();
        originalRadii.addAll(targetRadii);
        originalSpeed.clear();
        originalSpeed.addAll(targetSpeed);
    }

    private void mapCurrent(){
        int step = counter%transitionSteps;

        currentRadii.clear();
        currentSpeed.clear();

        for (int i=0 ; i<this.depth; i++){
            currentRadii.add(PApplet.map(step, 0, transitionSteps, originalRadii.get(i), targetRadii.get(i)));
            currentSpeed.add(PApplet.map(step, 0, transitionSteps, originalSpeed.get(i), targetSpeed.get(i)));
        }
    }

    public void makeGears() {
        for (int i = 0; i < this.depth; i++) {
            gears.get(i).setRadius(currentRadii.get(i));
            gears.get(i).setRotationSpeed(currentSpeed.get(i));
        }
    }

    public void prepareGears() {
        for (int i = 0; i<this.depth; i++){
            Gear tempGear = new Gear (new Vec2D(0,0), 100);
            gears.add(tempGear);
        }
        for (int i = this.depth-2; i>=0; i--){
            gears.get(i).setInner(gears.get(i + 1));
        }
        gears.get(0).set(new Vec2D(parent.width/2, parent.height/2));
        gear = gears.get(0);
    }


    public ArrayList<Vec2D> getPoints(int count){
        ArrayList <Vec2D> output = new ArrayList<Vec2D>();
        for (int i=0; i<count; i++){
            Vec2D currentPoint = gear.getPoint();
            output.add(currentPoint);
            gear.update();
        }
        gear.resetRotation();
        return output;
    }

    public void displayCurrentSpirograph(int count, PGraphics buffer){
        ArrayList<Vec2D> points = getPoints(count);
        buffer.pushStyle();
        buffer.strokeWeight(4f);
        buffer.beginShape(PConstants.POINTS);
        buffer.noFill();
//        buffer.camera(parent.width/2,parent.height/2,parent.height/2, 0,parent.height,0, 0,1,0);

        int fade = 100;
        for (int i=0; i<points.size();i++){
            if (i>=0 && i<=fade){
                buffer.stroke(parent.lerpColor(parent.color(255, 0), parent.color(255, 255), PApplet.map(i, 0, fade, 0, 1)));
            } else{
                if (i>=points.size()-fade && i<=points.size()){
                    buffer.stroke(parent.lerpColor(parent.color(255, 0), parent.color(255, 255), PApplet.map(i, points.size() - fade, points.size(), 1, 0)));
                }else{
                    buffer.stroke(255);
                }
            }
            buffer.vertex(points.get(i).x, points.get(i).y);
        }
        buffer.endShape();
        buffer.popStyle();
    }

    public void randomize() {
        //TODO: strange behavior
        setOriginal();
        randomizeTarget();
    }

    @Override
    public int getCounter() {
        return 0;
    }

    @Override
    public PShader getShader() {
//        bufferShader.setFPS((int)parent.frameRate);
        return bufferShader.getShader(counter);
    }

    @Override
    public boolean isDirect() {
        return direct;
    }
}
