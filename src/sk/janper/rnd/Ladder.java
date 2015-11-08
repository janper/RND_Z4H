package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PShape;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;

/**
 * Created by rndzvuk on 8.11.2015.
 */
public class Ladder extends VerletParticle {
    private PApplet parent;
    private PShape shape;
    private float overalWidth = 100f;
    private float barWidth = 10f;
    private float stepHeight = 60f;
    private int steps = 51;
    private int strokeColor;
    private float strokeWeight = 4f;

    private Vec3D currentPosition;


    public Ladder(PApplet parent, ReadonlyVec3D readonlyVec3D, float width, float stepHeight) {
        super(readonlyVec3D);
        this.parent = parent;
        this.overalWidth = width;
        this.stepHeight = stepHeight;
        strokeColor = parent.color(255);
        makeShape();
        currentPosition = new Vec3D (this);
    }

    public void smoothJitter(float amount){
        currentPosition.jitter(amount);
    }

    private void makeShape() {
        shape = parent.createShape(PApplet.GROUP);
        float halfHeight = (float)(steps-1)/2*(stepHeight+barWidth);

        for (int i=0; i<steps; i++){
            PShape rect = parent.createShape(PApplet.RECT,-overalWidth/2+barWidth , -halfHeight + i*(stepHeight+barWidth), overalWidth-barWidth*2, stepHeight);
            rect.setStroke(strokeColor);
            rect.setStrokeWeight(strokeWeight);
            rect.setFill(false);
            shape.addChild(rect);
        }

        PShape rect = parent.createShape(PApplet.RECT,-overalWidth/2 , -halfHeight-barWidth, overalWidth , 2*(halfHeight+barWidth));
        rect.setStroke(strokeColor);
        rect.setStrokeWeight(strokeWeight);
        rect.setFill(false);
        shape.addChild(rect);

//        shape.addChild(leftBar);
//        shape.addChild(rightBar);
    }


    public void displayDirect(){
        parent.pushMatrix();
//        currentPosition.interpolateToSelf(this, 0.01f);
        parent.translate(x, y,z);
        parent.shape(shape, 0,0);
        parent.popMatrix();
    }

}
