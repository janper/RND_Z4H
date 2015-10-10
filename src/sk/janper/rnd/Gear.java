package sk.janper.rnd;

import toxi.geom.ReadonlyVec2D;
import toxi.geom.Vec2D;

/**
 * Created by Jan on 23.5.2015.
 */
public class Gear extends Vec2D {
    private float rotation =0;
    private float rotationSpeed = 0;
    private float radius;
    private Gear inner = null;

    public Gear(ReadonlyVec2D readonlyVec2D, float radius) {
        super(readonlyVec2D);
        this.radius = radius;
    }

    public void update(){
        this.rotation+=this.rotationSpeed;
        if (inner!=null){
            inner.update();
        }
    }

    public Vec2D getPoint(){
        return getPoint(this, Vec2D.Y_AXIS.copy().normalizeTo(this.radius));
    }

    public Vec2D getPoint(Vec2D outerAbsoluteLocation, ReadonlyVec2D outerAbsoluteDirection){
        Vec2D newLocation = outerAbsoluteLocation.add(outerAbsoluteDirection.copy().normalizeTo(outerAbsoluteDirection.magnitude()-this.radius));
        Vec2D newDirection = outerAbsoluteDirection.copy().rotate(this.rotation).normalizeTo(this.radius);
        if (this.inner!=null){
            return inner.getPoint(newLocation, newDirection);
        } else {
            return newLocation.add(newDirection);
        }
    }

    public void resetRotation(){
        this.rotation=0;
        if (inner!=null){
            inner.resetRotation();
        }
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float roationSpeed) {
        this.rotationSpeed = roationSpeed;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Gear getInner() {
        return inner;
    }

    public void setInner(Gear inner) {
        this.inner = inner;
    }
}
