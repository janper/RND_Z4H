package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

/**
 * Created by Jan on 07.08.2015.
 */
public class Light extends Vec3D {
    private PShape shape;
    private PImage halo;
    private PImage full;
    private PApplet parent;
    private float span = 600;
    private Vec3D motionVector = new Vec3D(0,0,-1);
    private Vec3D ideal = new Vec3D(0,0,-1);
    private PGraphics tempGraphics;
    private float rotationAngle = 0f;

    private Vec3D offset = new Vec3D();

    public Light(ReadonlyVec3D readonlyVec3D, PApplet parent, String imageFile) {
        super(readonlyVec3D);
        this.parent = parent;
        generateShape(imageFile);
    }

    public Vec3D getOffset() {
        return offset;
    }

    public void setOffset(Vec3D offset) {
        this.offset = offset;
    }

    public float getSpan() {
        return span;
    }

    public void setSpan(float span) {
        this.span = span;
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public Vec3D getMotionVector() {
        return motionVector;
    }

    public void setMotionVector(Vec3D motionVector) {
        this.motionVector = motionVector;
    }

    public Vec3D getIdeal() {
        return ideal;
    }

    public void setIdeal(Vec3D ideal) {
        this.ideal = ideal;
    }

    private void generateShape(String imageFile){
        try {
            shape = parent.loadShape(imageFile);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public PImage getImage(){
        return full;
    }

    public void display(){
        parent.pushMatrix();
        parent.translate(x+offset.x, y+offset.y, z+offset.z);
        parent.rotateY(rotationAngle);

        parent.shape(shape, -span / 2, 0, shape.width, shape.height);
        parent.shape(shape, span / 2, 0, shape.width, shape.height);
        parent.popMatrix();
    }

    public PGraphics draw() {
        PGraphics canvas = parent.createGraphics(parent.width, parent.height);
        canvas.pushMatrix();
        canvas.translate(x, y, z);
        canvas.rotateY(rotationAngle);

        canvas.shape(shape, -span / 2, 0, shape.width, shape.height);
        canvas.shape(shape, span / 2, 0, shape.width, shape.height);
        canvas.popMatrix();
        return canvas;
    }



    public void update(){
        this.addSelf(motionVector);
        motionVector.interpolateToSelf(ideal, 0.1f);
//        System.out.print("Motion: " + motionVector.toString());
//        System.out.println(" Ideal: " + ideal.toString());
    }



}
