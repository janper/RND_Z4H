package sk.janper.rnd;

import processing.core.*;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;

/**
 * Created by Jan on 4.5.2015.
 */
public class Diamond extends VerletParticle {

    private PApplet parent;
    private PShape s;
    private PImage i;
    public Vec3D defaultPosition;


    Diamond (PApplet parent, float x, float y, float z){
        super(x, y, z);
        this.parent = parent;
        this.loadDefaultImage();
        this.setDefaultPosition();
    }

    Diamond (PApplet parent,Vec3D position){
        super(position);
        this.parent = parent;
        this.loadDefaultImage();
        this.drawDiamond();
        this.setDefaultPosition();
    }

    public void setDefaultPosition() {
        this.defaultPosition = this.copy();
    }

    public void setDefaultPosition (Vec3D position){
        this.defaultPosition = position;
    }

    public void loadDefaultImage() {
//        this.loadShape("diamond3.svg");
        this.loadImage("diamond.png");
    }

    public void loadShape(String fileName) {
        this.s = parent.loadShape(fileName);
    }

    public void loadImage(String fileName) {
        this.i = parent.loadImage(fileName);
    }

    public void adjustPosition(float ratio){
        this.interpolateToSelf(this.defaultPosition,ratio);
    }

    private void drawDiamond(){
        PShape mainShape = parent.createShape();

        mainShape.beginShape();
        mainShape.stroke(255);
        mainShape.strokeWeight(5f);
//        mainShape.fill(200, 225, 255, 50f);
        mainShape.noFill();
//        mainShape.fill(32);
        mainShape.vertex(-50, 0, 0);
        mainShape.vertex(-50 + 12.5f, -25, 0);
        mainShape.vertex(50 - 12.5f, -25, 0);
        mainShape.vertex(50, 0, 0);
        mainShape.vertex(0, 75, 0);
        mainShape.endShape(PConstants.CLOSE);

        PShape edgeShape = parent.createShape();

        edgeShape.beginShape();
        edgeShape.stroke(255);
        edgeShape.strokeWeight(5f);
        edgeShape.noFill();
        edgeShape.vertex((float) (-50 + 12.5 + 75 / 4), -25, 0);
        edgeShape.vertex((float)(-50+100/4), 0, 0);
        edgeShape.vertex(0, 75, 0);
        edgeShape.vertex((float)(50-100/4), 0, 0);
        edgeShape.vertex((float) (50 - 12.5 - 75 / 4), -25, 0);
        edgeShape.endShape(PConstants.CLOSE);

        PShape creaseShape = parent.createShape();

        creaseShape.beginShape(PConstants.LINES);
        creaseShape.stroke(255);
        creaseShape.strokeWeight(5f);
        creaseShape.noFill();
        creaseShape.vertex(-50, 0, 0);
        creaseShape.vertex(50, 0, 0);
        creaseShape.endShape();

        this.s = parent.createShape(PConstants.GROUP);
        this.s.addChild(mainShape);
        this.s.addChild(creaseShape);
        this.s.addChild(edgeShape);
    }

    public void display(){
        parent.pushStyle();
        parent.pushMatrix();
        parent.translate(this.x, this.y, this.z);

        float diamondWidth = 50f;
        float diamondHeight = 40f;

        parent.stroke(255);
        parent.strokeWeight(5f);
        parent.noFill();

//        parent.shape(this.s, diamondWidth/(-2),diamondHeight/(-2),diamondWidth, diamondHeight);
        parent.shape(this.s, 0, 0,diamondWidth, diamondHeight);

        parent.popMatrix();
        parent.popStyle();

    }


    public void display(Vec3D normal, PGraphics buffer){

        buffer.pushStyle();
        buffer.pushMatrix();


        float zRotation = Vec3D.Y_AXIS.angleBetween(new Vec3D(normal.x,normal.y, 0));
//        float xRotation = Vec3D.Y_AXIS.angleBetween(new Vec3D(0,normal.y,normal.z));
//        float yRotation = Vec3D.Y_AXIS.angleBetween(new Vec3D(normal.x,0,normal.z));

//        parent.rotateX(xRotation);
//        parent.rotateY(yRotation);

        PMatrix3D transformationMatrix = new PMatrix3D();
        transformationMatrix.translate(this.x, this.y, this.z);
//        transformationMatrix.rotateZ(zRotation);

//        Vec3D secondPoint = this.add(normal);
//        parent.stroke(255);
//        parent.strokeWeight(1f);
//        parent.line (this.x, this.y, this.z, secondPoint.x, secondPoint.y, secondPoint.z);

        buffer.applyMatrix(transformationMatrix);

        float diamondWidth = 60f;
        float diamondHeight = 40f;

//        parent.stroke(255);
//        parent.strokeWeight(5f);
//        parent.noFill();

//        parent.shape(this.s, diamondWidth/(-2),diamondHeight/(-2),diamondWidth, diamondHeight);
        buffer.shape(this.s, 0, 0,diamondWidth, diamondHeight);
//        parent.image(this.i, 0, 0, diamondWidth, diamondHeight);

        buffer.popMatrix();
        buffer.popStyle();

    }

    public void display(Vec3D normal) {
        parent.pushStyle();
        parent.pushMatrix();
        
        float zRotation = Vec3D.Y_AXIS.angleBetween(new Vec3D(normal.x,normal.y, 0));

        PMatrix3D transformationMatrix = new PMatrix3D();
        transformationMatrix.translate(this.x, this.y, this.z);

        parent.applyMatrix(transformationMatrix);

        float diamondWidth = 60f;
        float diamondHeight = 40f;
        parent.shape(this.s, 0, 0,diamondWidth, diamondHeight);

        parent.popMatrix();
        parent.popStyle();

    }
}
