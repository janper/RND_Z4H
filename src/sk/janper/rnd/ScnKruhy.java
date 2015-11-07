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
    private final PShader halftoneShader;
    private PApplet parent;
    private String name = "Kruhy v obili";
    private int bgColour;

    private boolean moving = false;

    private Gear gear;

    private int depth = 4;
    private ArrayList<Float> originalRadii = new ArrayList<>();
    private ArrayList<Float> currentRadii = new ArrayList<>();
    private ArrayList<Float> targetRadii = new ArrayList<>();

    private ArrayList<Float> originalSpeed = new ArrayList<>();
    private ArrayList<Float> currentSpeed = new ArrayList<>();
    private ArrayList<Float> targetSpeed = new ArrayList<>();

    private ArrayList<Gear> gears = new ArrayList<>();

    private final int TRANSITION_STEPS = 50000;

    private BufferShader bufferShader;

    private int counter;
    private boolean direct = false;

    private int mode = 0;
    private float z = 0;

    private boolean finished = false;


    public ScnKruhy(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        bufferShader = new BuffKruhy(parent);
        reset();
        randomize();
        halftoneShader = parent.loadShader("halftone.glsl");
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

//        if (bufferShader.isJustAnim(counter)){
//            setDirectTrue();
//            display();
//        } else {
//            direct = false;
            buffer.beginDraw();
            buffer.clear();
            buffer.camera(parent.width / 2f, parent.height*1.15f, (parent.height / 2f) / PApplet.tan(PApplet.PI * 30f / 180f)*0.3f, parent.width / 2f, parent.height / 1.5f, 0, 0, 1, 0);
            displayCurrentSpirograph(1500, buffer);
            buffer.endDraw();
//        }
    }

    @Override
    public void display() {
        if (moving){
            counter++;
        }

        mapCurrent();
        makeGears();
        
        displayCurrentSpirograph(1500);

//        if (!bufferShader.isJustAnim(counter)){
//            parent.resetShader();
//            direct = false;
//        }
    }

    private void displayCurrentSpirograph(int count) {
        ArrayList<Vec2D> points = getPoints(count);
        parent.pushStyle();
        parent.strokeWeight(8f);
        parent.beginShape(PConstants.POINTS);
        parent.noFill();
//        parent.camera(parent.width/2,parent.height/2,parent.height/2, 0,parent.height,0, 0,1,0);
        parent.camera(parent.width / 2f, parent.height*1.25f, (parent.height / 2f) / PApplet.tan(PApplet.PI * 30f / 180f)*0.5f, parent.width /2f, parent.height / 1.5f, 0, 0, 1, 0);
//        parent.camera(parent.width / 2f, parent.height/3f , (parent.height / 2f) / parent.tan(parent.PI * 30f / 180f)*0.25f, parent.width / 2f, parent.height / 1.5f, 0, 0, 1, 0);

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

//            float z = 0;

            if (mode==9){
//                z=this.z;
//                this.z+=0.01f;
                targetRadii.set(0, targetRadii.get(0)*1.1f);
//
//                gear.setRadius(gear.getRadius()*1.1f);
            }

            parent.vertex(points.get(i).x, points.get(i).y, 0);
        }
        parent.endShape();
        parent.popStyle();
    }

    @Override
    public void reset() {
        prepareGears();
        randomizeTarget();
        counter = 0;
        mode =0;
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
        mode = which;
        if (mode!=9){
//            this.z=0;
            targetRadii.set(0, 350f);
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
        int step = counter% TRANSITION_STEPS;

        currentRadii.clear();
        currentSpeed.clear();

        for (int i=0 ; i<this.depth; i++){
            currentRadii.add(PApplet.map(step, 0, TRANSITION_STEPS, originalRadii.get(i), targetRadii.get(i)));
            currentSpeed.add(PApplet.map(step, 0, TRANSITION_STEPS, originalSpeed.get(i), targetSpeed.get(i)));
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
        ArrayList <Vec2D> output = new ArrayList<>();
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
        buffer.strokeWeight(6f);
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

            if (mode==9){
//                gear.setRadius(gear.getRadius()*1.1f);
                if (targetRadii.get(0)<1000000) {
                    float newRadius = targetRadii.get(0) * 1.00001f;
                    targetRadii.set(0, newRadius);
                } else {
                    finished = true;
                }
            }

            buffer.vertex(points.get(i).x, points.get(i).y, 0);
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
