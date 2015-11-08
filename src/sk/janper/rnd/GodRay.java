package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 08.08.2015.
 */
public class GodRay extends Vec3D {
    private PApplet parent;

    private int color1;
    private int color2;

    private float weight1;
    private float weight2;

    private float stickDistance1;
    private float stickDistance2;

    private float updateSpeed = 0.025f;

    private int counter = 0;

    private ArrayList<Vec3D> points;
    private ArrayList<Vec3D> currentPoints;
    private float growSteps = 60*5;

    private float initSize = 0.25f;
    private float growthRatio = 1.0001f;

    public GodRay(PApplet parent, ReadonlyVec3D location, ReadonlyVec3D target, int subdivisions) {
        super(location);
        this.parent=parent;
        makeRay(location, target, subdivisions);
        alignCurrent();
        color1 = parent.color(255,200);
        color2 = parent.color(255,255);
        weight1 = 8;
        weight2 = 0;
    }

    public void makeRay(ReadonlyVec3D location, ReadonlyVec3D target, int subdivisions) {
        points = new ArrayList<Vec3D>();
        for (int i=0; i<=subdivisions; i++){
            Vec3D tempPoint = location.interpolateTo(target,i*1f/subdivisions);
            points.add(tempPoint);
        }
    }

    private void alignCurrent(){
        currentPoints = new ArrayList<Vec3D>();
        points.forEach(p -> currentPoints.add(p.copy()));
    }

    public int getColor1() {
        return color1;
    }

    public void setColor1(int color1) {
        this.color1 = color1;
    }

    public int getColor2() {
        return color2;
    }

    public void setColor2(int color2) {
        this.color2 = color2;
    }

    public float getWeight1() {
        return weight1;
    }

    public void setWeight1(float weight1) {
        this.weight1 = weight1;
    }

    public float getWeight2() {
        return weight2;
    }

    public void setWeight2(float weight2) {
        this.weight2 = weight2;
    }

    public float getStickDistance1() {
        return stickDistance1;
    }

    public void setStickDistance1(float stickDistance1) {
        this.stickDistance1 = stickDistance1;
    }

    public float getStickDistance2() {
        return stickDistance2;
    }

    public void setStickDistance2(float stickDistance2) {
        this.stickDistance2 = stickDistance2;
    }

    public ArrayList<Vec3D> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Vec3D> points) {
        this.points = points;
    }

    public float getUpdateSpeed() {
        return updateSpeed;
    }

    public void setUpdateSpeed(float updateSpeed) {
        this.updateSpeed = updateSpeed;
    }

    public void update(){
        counter++;
        for (int i=0; i<points.size(); i++){
            currentPoints.get(i).interpolateToSelf(points.get(i), updateSpeed);
        }
    }

    public void jitterRay (float strength){
        currentPoints.forEach(p -> p.jitter(strength));
    }

    public void alignTo (ArrayList<Vec3D> refPoints){
        float stickDistance1Squared = stickDistance1*stickDistance1;
        float stickDistance2Squared = stickDistance2*stickDistance2;
        ArrayList<Vec3D> newPoints = new ArrayList<Vec3D>();
        points.forEach( p -> {
            ArrayList<Vec3D> valid = new ArrayList<Vec3D>();
            refPoints.forEach(r -> {
                if (p.distanceToSquared(r)>=stickDistance1Squared && p.distanceToSquared(r)<=stickDistance2Squared){
                    valid.add(r);
                }
            });
            if (valid.size()>0){
                int rand = (int)Math.floor(Math.random()*valid.size());
                Vec3D picked = valid.get(rand);
                newPoints.add(picked);
            } else {
                newPoints.add(p);
            }
        });
        points = newPoints;
    }

    public void alignTo (){
        ArrayList<Vec3D> newPoints = new ArrayList<>();
        points.forEach( p -> {
            Vec3D randomVector = Vec3D.randomVector().normalizeTo((float)Math.random()*(stickDistance2-stickDistance1)+stickDistance1);
            newPoints.add(p.add(randomVector));
        });
        points = newPoints;
    }


    public void display(PGraphics buffer){
        buffer.pushMatrix();
        buffer.pushStyle();
        for (int i=0; i<currentPoints.size()-1; i++){
            buffer.strokeWeight(PApplet.map(i, 0, currentPoints.size()-1, weight1, weight2));
            buffer.stroke (parent.lerpColor(color1, color2, (i/currentPoints.size()-1)));
            buffer.line (currentPoints.get(i).x, currentPoints.get(i).y, currentPoints.get(i).z,currentPoints.get(i+1).x, currentPoints.get(i+1).y, currentPoints.get(i+1).z);
        }
        buffer.popStyle();
        buffer.popMatrix();
    }

    public void display() {
        parent.pushMatrix();
        parent.pushStyle();

        ArrayList<Vec3D> relativePoints = new ArrayList<>();
        for (int i=0; i<currentPoints.size()-1; i++) {
            relativePoints.add(currentPoints.get(i+1).sub(currentPoints.get(i)));
        }

        Vec3D first = currentPoints.get(0);
        Vec3D second;

        for (int i=0; i<currentPoints.size()-1; i++){
            parent.strokeWeight(PApplet.map(i, 0, currentPoints.size()-1, weight1, weight2));
            parent.stroke (parent.lerpColor(color1, color2, (i/currentPoints.size()-1)));
//            float percent = (float) counter / growSteps;
            initSize *= growthRatio;
            if (initSize<1) {
                second = first.add(relativePoints.get(i).scale(initSize));
            } else {
                second = first.add(relativePoints.get(i));
            }
            parent.line (first.x, first.y, first.z,second.x, second.y, second.z);
            first = new Vec3D(second);
        }
        parent.popStyle();
        parent.popMatrix();
    }
}
