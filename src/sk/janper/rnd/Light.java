package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

/**
 * Created by Jan on 07.08.2015.
 */
public class Light extends Vec3D {
    public PShape shape;
    public PApplet parent;
    public float span = 300;
    private Vec3D motionVector = new Vec3D(0,0,-1);
    private Vec3D ideal = new Vec3D(0,0,-1);
    public float rotationAngle = 0f;

    public Vec3D offset = new Vec3D();

    private int mode = 0;

    private int colour;
    private int haloColour;

    public Light(ReadonlyVec3D readonlyVec3D, PApplet parent) {
        this (readonlyVec3D, parent, "");
    }

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

    public void generateShape(String imageFile){
        try {
            shape = parent.loadShape(imageFile);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void display(PGraphics buffer){
        buffer.pushMatrix();
        buffer.translate(x+offset.x, y+offset.y, z+offset.z);
        buffer.rotateY(rotationAngle);

        buffer.shape(shape, -span / 2, 0, shape.width, shape.height);
        buffer.shape(shape, span / 2, 0, shape.width, shape.height);
        buffer.popMatrix();
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



    public void update(float speed){
        this.addSelf(motionVector.scale(speed));
        motionVector.interpolateToSelf(ideal, 0.1f);
//        System.out.print("Motion: " + motionVector.toString());
//        System.out.println(" Ideal: " + ideal.toString());
    }


    public void display() {
        parent.pushMatrix();
        parent.translate(x+offset.x, y+offset.y, z+offset.z);
        parent.rotateY(rotationAngle);

        float weight;

        if (mode == 0){
            weight = PApplet.map(z + offset.z, -4500, 4500, 1f, 20f);
        } else {
            weight = 8f;
        }

//        shape.setStroke(getHaloColour());
//        shape.setStrokeWeight(weight+10f);
//        parent.shape(shape, -span / 2, 0, shape.width, shape.height);
//        parent.shape(shape, span / 2, 0, shape.width, shape.height);
//
//        parent.translate(0, 0, 2f);

        shape.setStroke(getColour());
        shape.setStrokeWeight(weight);
        parent.shape(shape, -span / 2, 0, shape.width, shape.height);
        parent.shape(shape, span / 2, 0, shape.width, shape.height);

        parent.popMatrix();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public int getHaloColour() {
        return haloColour;
    }

    public void setHaloColour(int haloColour) {
        this.haloColour = haloColour;
    }
}
