package sk.janper.rnd;

import processing.core.PApplet;
import toxi.geom.ReadonlyVec3D;

/**
 * Created by rndzvuk on 8.11.2015.
 */
public class TailLight extends Light {
//    PShape shape;

    public TailLight(ReadonlyVec3D readonlyVec3D, PApplet parent) {
        super(readonlyVec3D, parent);
//        makeFrontLight();
    }

    public void generateShape(String s) {
//        PShape leftShape = parent.createShape(PApplet.ELLIPSE, -100,0, 100f, 50f);
//        leftShape.setStroke(parent.color(255,0,0));
//        leftShape.setStrokeWeight(10f);
//        leftShape.setFill(false);
//
//        PShape rightShape = parent.createShape(PApplet.ELLIPSE, 100,0, 100f, 50f);
//        rightShape.setStroke(parent.color(255,0,0));
//        rightShape.setStrokeWeight(10f);
//        rightShape.setFill(false);
//
//        shape = parent.createShape(PApplet.GROUP);
//        shape.addChild(leftShape);
//        shape.addChild(rightShape);

        this.setColour(parent.color(255,0,0));
        this.setHaloColour(parent.color(255,0,0, 64));

        shape = parent.createShape(PApplet.ELLIPSE, 0,0, 100f, 50f);
        shape.setStroke(this.getColour());
        shape.setStrokeWeight(10f);
        shape.setFill(false);
    }
}