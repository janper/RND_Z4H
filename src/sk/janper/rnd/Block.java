package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jan on 22.5.2015.
 */
public class Block extends Vec3D {
    private Vec3D originalPosition;
    private float leave = 0f;
    private boolean used = false;
    private Vec3D size = new Vec3D(100f,50f,100f);
    private PShape shape;
    private ArrayList<Block> underneath = new ArrayList<Block>();
    private ArrayList<Block> everything;
    private int layer;
    private PApplet parent;
    private int strokeColour;
    private int bgColour;
    private float strokeWeight;


    public Block(PApplet parent, ReadonlyVec3D readonlyVec3D, int layer, Vec3D size, ArrayList<Block> everything, int colour, float weight, int bgColour) {
        super(readonlyVec3D);
        this.layer = layer;
        this.parent = parent;
        this.everything = everything;
        this.size = size;
        this.originalPosition = this.copy();
        this.strokeColour = colour;
        this.strokeWeight=weight;
        this.bgColour = bgColour;

        if (this.layer>0) {
            addBelow();
        }
    }

    public int getStrokeColour() {
        return strokeColour;
    }

    public void setStrokeColour(int strokeColour) {
        this.strokeColour = strokeColour;
    }

    public float getStrokeWeight() {
        return strokeWeight;
    }

    public void setStrokeWeight(float strokeWeight) {
        this.strokeWeight = strokeWeight;
    }

    private void addBelow() {
        float nextX = this.size.x / 2;
        float nextY = this.size.y;
        float nextZ = this.size.z / 2;

        ArrayList<Block> children = new ArrayList<Block>();
        children.add(new Block(this.parent, new Vec3D(this.x + nextX, this.y + nextY, this.z + nextZ), this.layer-1, this.size, this.everything, strokeColour, strokeWeight, bgColour));
        children.add(new Block(this.parent, new Vec3D(this.x - nextX, this.y + nextY, this.z + nextZ), this.layer - 1, this.size, this.everything, strokeColour, strokeWeight, bgColour));
        children.add(new Block(this.parent, new Vec3D(this.x + nextX, this.y + nextY, this.z - nextZ), this.layer - 1, this.size, this.everything, strokeColour, strokeWeight, bgColour));
        children.add(new Block(this.parent, new Vec3D(this.x - nextX, this.y + nextY, this.z - nextZ), this.layer - 1, this.size, this.everything, strokeColour, strokeWeight, bgColour));


        List<Block> filteredEverything = this.everything.parallelStream().filter(k -> (k.getLayer() < this.layer)).collect(Collectors.toList());
        children.forEach(b -> {
            Block similar = b.findSimilar(filteredEverything);
            if (similar==null){
                this.everything.add(b);
                this.underneath.add(b);
            } else {
                this.underneath.add(similar);
            }
        });

    }

    public void leave (float howMuch){
        float currentLeave = parent.random(howMuch, howMuch*2f);
        originalPosition.y-=currentLeave;
        leave+=currentLeave;
    }

    public void comeBack (){
        originalPosition.y+=leave;
        leave=0;
    }

    public void adjust(float factor){
        this.interpolateToSelf(this.originalPosition,factor);
    }

    public void display(PGraphics buffer){
//        if (this.shape ==null) {
//            displayDots(buffer);
//        } else {
            displayShapes(buffer);
//        }
    }

    public void displayShapes(PGraphics buffer) {
        if (this.used) {
//            System.out.print(".");
            buffer.pushMatrix();
            buffer.pushStyle();
            buffer.translate(this.x, this.y, this.z);
//            buffer.shape(this.shape);

            buffer.noFill();
            buffer.stroke(strokeColour);
            buffer.strokeWeight(strokeWeight);
            buffer.fill(bgColour);
            buffer.box(size.x, size.y, size.x);
//            System.out.println("Weight: "+strokeWeight+" Colour: "+strokeColour+" Position: "+this.toString()+" Size: "+size.toString());

            buffer.popStyle();
            buffer.popMatrix();
        }
    }

    public void displayDots(PGraphics buffer) {
        if (this.used) {
            buffer.pushStyle();
            buffer.stroke(255);
            buffer.strokeWeight(5f);
            buffer.point(this.x, this.y, this.z);
            buffer.popStyle();
        }
    }

    public Block findSimilar(List<Block> list){
        if ((list!=null)&&(!list.isEmpty())) {
            final Comparator<Block> comp = (b1, b2) -> Float.compare(b1.distanceToSquared(this), b2.distanceToSquared(this));
            Block closest = list.parallelStream()
                    .min(comp)
                    .get();
            return (closest.distanceToSquared(this) > 0) ? null : closest;
        } else{
            return null;
        }

    }


    public ArrayList<Block> getEverythinig() {
        return everything;
    }

    public void setEverythinig(ArrayList<Block> everythinig) {
        this.everything = everythinig;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Vec3D getSize() {
        return size;
    }

    public void setSize(Vec3D size) {
        this.size = size;
    }

    public PShape getShape() {
        return shape;
    }

    public void setShape(PShape shape) {
        this.shape = shape;
    }

    public ArrayList<Block> getUnderneath() {
        return underneath;
    }

    public void setUnderneath(ArrayList<Block> underneath) {
        this.underneath = underneath;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
