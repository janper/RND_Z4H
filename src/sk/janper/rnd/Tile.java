package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import toxi.geom.ReadonlyVec2D;
import toxi.geom.Vec2D;

/**
 * Created by Jan on 08.08.2015.
 */
public class Tile extends Vec2D {
    private int w = 120;
    private int h = 120;

    private Vec2D target;

    private PGraphics graphics;
    private PShape shape;

    private PApplet parent;

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public Vec2D getTarget() {
        return target;
    }

    public void setTarget(Vec2D target) {
        this.target = target;
    }

    public Tile(ReadonlyVec2D readonlyVec2D, int w, int h, PApplet parent) {
        super(readonlyVec2D);
        this.w = w;
        this.h = h;
        this.parent = parent;
        this.target = this.copy();
        makeGraphics();
//        makeShape();
    }

    private void makeGraphics(){
        graphics = parent.createGraphics(w,h);
        graphics.beginDraw();
        graphics.beginShape();
        graphics.stroke(255);
        graphics.strokeWeight(2);
        graphics.noFill();
        graphics.vertex(5, 5);
        graphics.vertex(5, h - 5);
        graphics.vertex(w - 5, h - 5);
        graphics.vertex(w - 5, 5);
        graphics.endShape(PConstants.CLOSE);

        graphics.beginShape();
        graphics.stroke(255);
        graphics.strokeWeight(2);
        graphics.noFill();
        graphics.vertex(w/2, h - 10);
        graphics.vertex(w - 10, h - 10);
        graphics.vertex(w - 10, h/2);
        graphics.endShape();

        graphics.endDraw();
    }

    private void makeShape(){
        shape = parent.createShape(w,h);
        shape.beginShape();
        shape.stroke(255);
        shape.strokeWeight(3);
        shape.noFill();
        shape.vertex(5, 5);
        shape.vertex(5, h - 5);
        shape.vertex(w-5, h-5);
        shape.vertex(w-5, 5);
        shape.endShape(PConstants.CLOSE);
    }

    public void update(){
        this.interpolateToSelf(target,0.1f);
    }

    public void display(PGraphics buffer){
        buffer.image(graphics, x, y);
//        parent.shape(shape,x,y);
    }

    public void display() {
        parent.image(graphics, x, y);
    }
}
