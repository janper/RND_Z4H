package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.Vec2D;

import java.util.ArrayList;

/**
 * Created by Jan on 08.10.2015.
 */
public class ScnKruhy implements Scene {
    private PApplet parent;
    private String name = "Kruhy v obilí";
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

    private int transitionSteps = 4800;

    private int counter;


    public ScnKruhy(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
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

        buffer.beginDraw();
        buffer.clear();

        displayCurrentSpirograph(1500, buffer);

        buffer.endDraw();
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
            targetSpeed.add(parent.random(-parent.PI / ((this.depth - i) * 10), parent.PI / ((this.depth - i) * 10)));
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
            currentRadii.add(parent.map(step, 0, transitionSteps, originalRadii.get(i), targetRadii.get(i)));
            currentSpeed.add(parent.map(step, 0, transitionSteps, originalSpeed.get(i), targetSpeed.get(i)));
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
        buffer.beginShape(parent.POINTS);
        buffer.noFill();

        int fade = 100;
        for (int i=0; i<points.size();i++){
            if (i>=0 && i<=fade){
                buffer.stroke(parent.lerpColor(parent.color(255, 0), parent.color(255, 255), parent.map(i, 0, fade, 0, 1)));
            } else{
                if (i>=points.size()-fade && i<=points.size()){
                    buffer.stroke (parent.lerpColor(parent.color(255, 0), parent.color(255, 255), parent.map(i, points.size() - fade, points.size(), 1, 0)));
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
        setOriginal();
        randomizeTarget();
    }

    @Override
    public PGraphics getBack(){
        return null;
    }

    @Override
    public PGraphics getFront(){
        return null;
    }

    @Override
    public int getCounter() {
        return 0;
    }
}
