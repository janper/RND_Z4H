package sk.janper.rnd;

import toxi.geom.ReadonlyVec2D;
import toxi.geom.Vec2D;

import java.util.ArrayList;

/**
 * Created by Jan on 29.6.2015.
 */
public class PolyCurve extends Vec2D {

    private Vec2D firstVector;
    private ArrayList <Float> targetAngles = new ArrayList<Float>();
    private ArrayList <Float> currentAngles = new ArrayList<Float>();
    private ArrayList <Float> differenceAngles = new ArrayList<Float>();


    private int segments = 10;
    private float updateFactor = 0.01f;
    private float targetTreshold = 0.1f;
    private float scale = 5f;
    private float changeFrequency = 0.5f;
    private float angleSpread = (float)Math.PI/8;

    private float rotation = 0f;

    private boolean reflect = false;
    private Vec2D reflectionPoint;
    private Vec2D reflectionDirection;
    private int updateSteps = 60*2;


    public PolyCurve() {
        super();
        setAllVectors();
    }

    public PolyCurve(ReadonlyVec2D readonlyVec3D) {
        super(readonlyVec3D);
        setAllVectors();
    }

    public int getSegments() {
        return segments;
    }

    public void setSegments(int segments) {
        this.segments = segments;
    }

    public float getUpdateFactor() {
        return updateFactor;
    }

    public void setUpdateFactor(float updateFactor) {
        this.updateFactor = updateFactor;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.firstVector = this.firstVector.getNormalizedTo(this.scale);
    }

    public float getTargetTreshold() {
        return targetTreshold;
    }

    public void setTargetTreshold(float targetTreshold) {
        this.targetTreshold = targetTreshold;
    }

    public Vec2D getFirstVector() {
        return firstVector;
    }

    public void setFirstVector(Vec2D firstVector) {
        this.firstVector = firstVector;
    }

    public float getAngleSpread() {
        return angleSpread;
    }

    public void setAngleSpread(float angleSpread) {
        this.angleSpread = angleSpread;
    }

    public float getChangeFrequency() {
        return changeFrequency;
    }

    public void setChangeFrequency(float changeFrequency) {
        this.changeFrequency = changeFrequency;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public boolean isReflect() {
        return reflect;
    }

    public void setReflect(boolean reflect) {
        this.reflect = reflect;
        if (this.reflect && this.reflectionPoint == null) {
            setReflectionPoint(this);
        }
        if (this.reflect && this.reflectionDirection == null) {
            setReflectionDirection(this.firstVector);
        }
    }

    public int getUpdateSteps() {
        return updateSteps;
    }

    public void setUpdateSteps(int updateSteps) {
        this.updateSteps = updateSteps;
        differenceAngles.clear();
        for (int i = 0; i < this.segments; i++) {
            this.differenceAngles.add(this.targetAngles.get(i) - this.currentAngles.get(i));
        }
    }

    public Vec2D getReflectionPoint() {
        return reflectionPoint;
    }

    public void setReflectionPoint(Vec2D reflectionPoint) {
        this.reflectionPoint = reflectionPoint;
    }

    public Vec2D getReflectionDirection() {
        return reflectionDirection;
    }

    public void setReflectionDirection(Vec2D reflectionDirection) {
        this.reflectionDirection = reflectionDirection;
    }

    public void setAllVectors (){
        this.firstVector = Vec2D.Y_AXIS.scale(this.scale);
        targetAngles.clear();
        currentAngles.clear();
        differenceAngles.clear();
        for (int i=0; i<this.segments;i++){
            this.targetAngles.add(0f);
            this.currentAngles.add(1f);
            this.differenceAngles.add(0f-1f);
        }
    }

    public void randomizeAngles() {
        randomizeAngles(this.angleSpread, this.changeFrequency);
    }

    public void randomizeAngles(float angleSpread, float changeFrequency){
        float angle = (float) Math.random()*(2*angleSpread)-angleSpread;
        targetAngles.clear();
        differenceAngles.clear();
        for (int i=0; i<this.segments;i++){
            this.targetAngles.add(angle);
            this.differenceAngles.add(angle-currentAngles.get(i));
            double randomChange = Math.random();
            if (randomChange<changeFrequency){
                angle+=(float) Math.random()*(2*angleSpread)-angleSpread;
            }
        }
    }

    public void update(float factor){
        ArrayList <Float> newCurrentAngles = new ArrayList<Float>();
        for (int i=0; i<targetAngles.size();i++) {
            newCurrentAngles.add(currentAngles.get(i)+(targetAngles.get(i)-currentAngles.get(i))*factor);
        }
        currentAngles=newCurrentAngles;
    }

    public void linearUpdate(int steps){
        ArrayList <Float> newCurrentAngles = new ArrayList<Float>();
        for (int i=0; i<targetAngles.size();i++) {
            newCurrentAngles.add(currentAngles.get(i)+differenceAngles.get(i)/steps);
        }
        currentAngles=newCurrentAngles;
    }

    public void linearUpdate(){
        linearUpdate(this.updateSteps);
    }

    public void update(){
        update(this.updateFactor);
    }

    public ArrayList<Vec2D> getPointPositions(ArrayList<Float> angles){
        ArrayList<Vec2D> output = new ArrayList<Vec2D>();
        output.add(this);
        Vec2D v = this.firstVector.getRotated(this.rotation);
        int direction = 1;
        if (isReflect()) {
//            v=v.getReflected(reflectionDirection);
            direction = -1;
        }
        for (int i=0; i<angles.size();i++){
            output.add(output.get(output.size()-1).add(v));
            v = v.getRotated(angles.get(i)*direction);
        }
        return output;
    }

    public ArrayList<Vec2D> getTargetPointPositions(){
        return getPointPositions(this.targetAngles);
    }

    public ArrayList<Vec2D> getCurrentPointPositions(){
        return getPointPositions(this.currentAngles);
    }

    public boolean isStable(){
        for (int i=0; i<targetAngles.size();i++) {
            if (Math.abs(currentAngles.get(i) - targetAngles.get(i))>this.targetTreshold){
                return false;
            }
        }
        return true;
    }

}
