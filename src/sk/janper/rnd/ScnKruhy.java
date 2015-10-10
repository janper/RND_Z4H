package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 08.10.2015.
 */
public class ScnKruhy implements Scene {
    private PApplet parent;
    private String name = "Kruhy v obilí";
    private int bgColour;

    private boolean moving = false;

    private boolean photographic = false;


    private PImage field, circles;
    private PGraphics alphaLayer;

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


    public ScnKruhy(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        field= parent.loadImage("pole3.jpg");
        circles= parent.loadImage("kruhy3.jpg");
        parent.stroke(255);
        parent.strokeWeight(2f);
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
    public void display() {
        if (moving){

        }

        mapCurrent();
        makeGears();

        if (photographic){
            parent.image(field, 0, 0);
            displayCurrentSpirographAlpha(1500);
        } else {
            parent.background(bgColour);
            displayCurrentSpirograph(1500);
        }
    }

    @Override
    public void reset() {
        alphaLayer = parent.createGraphics(circles.width, circles.height);
        alphaLayer.beginDraw();

        prepareGears();

        randomizeTarget();
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
        if (which==1){
            photographic = true;
        } else {
            photographic = false;
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
        int step = parent.frameCount%transitionSteps;

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

    public void displayCurrentSpirograph(int count){
        ArrayList<Vec2D> points = getPoints(count);
        parent.pushStyle();
        parent.strokeWeight(4f);
        parent.beginShape(parent.POINTS);
        parent.noFill();

        int fade = 100;
        for (int i=0; i<points.size();i++){
            if (i>=0 && i<=fade){
                parent.stroke(parent.lerpColor(parent.color(255, 0), parent.color(255, 255), parent.map(i, 0, fade, 0, 1)));
            } else{
                if (i>=points.size()-fade && i<=points.size()){
                    parent.stroke (parent.lerpColor(parent.color(255, 0), parent.color(255, 255), parent.map(i, points.size() - fade, points.size(), 1, 0)));
                }else{
                    parent.stroke(255);
                }
            }
            parent.vertex(points.get(i).x, points.get(i).y);
        }
        parent.endShape();
        parent.popStyle();
    }


    public void displayCurrentSpirographAlpha(int count){
        ArrayList<Vec2D> tempPoints = getPoints(count);
        ArrayList<Vec2D> points = new ArrayList<>();

        for (int i=0; i<tempPoints.size(); i+=10){
            points.add(tempPoints.get(i));
        }

        alphaLayer.background(0);
        alphaLayer.stroke(255);
        alphaLayer.noFill();
        float weight = parent.map(parent.mouseX, 0, parent.width, 10f,200f);
        alphaLayer.strokeWeight(weight);
        float offset = 5f;
        parent.pushStyle();
        parent.stroke(parent.color(0, 128));
        parent.strokeWeight(weight);
        points.forEach(p -> parent.point(p.x - offset, p.y - offset));
        parent.popStyle();


        points.forEach(p -> alphaLayer.point(p.x, p.y));



        circles.mask(alphaLayer.get());
        parent.image(circles, 0, 0);
    }

    public void randomize() {
        setOriginal();
        randomizeTarget();
    }



}
