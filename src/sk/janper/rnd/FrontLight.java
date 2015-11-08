package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PShape;
import toxi.geom.ReadonlyVec3D;

/**
 * Created by rndzvuk on 8.11.2015.
 */
public class FrontLight extends Light {
    PShape shape;

    public FrontLight(ReadonlyVec3D readonlyVec3D, PApplet parent) {
        super(readonlyVec3D, parent);
        makeFrontLight();
    }

    private void makeFrontLight() {

    }
}
