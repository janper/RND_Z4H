package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;
import toxi.geom.Vec3D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jan on 10.10.2015.
 */
public class ScnPyramidy implements Scene {
    public static final Vec3D SIZE = new Vec3D(100f, 50f, 100f);
    private final String name = "Pyramídy";
    public ArrayList <Block> blocks = new ArrayList<Block>();
    private PApplet parent;
    private int which = 0;
    private boolean showUnder = false;

    private Vec3D randomPoint;

    private boolean moving = false;
    private int speed = 3;

    public ScnPyramidy(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        makePyramids ();
        PShape shape = makeShape(SIZE, 255, parent.color(32,192), 3f);
        blocks.forEach(b -> b.setShape(shape));
        System.out.println(" done!");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() {
        moving = true;
    }

    @Override
    public void stop() {
        moving = false;
    }

    @Override
    public void display(PGraphics buffer) {
        if (moving) {
            blocks.forEach(b -> b.adjust(0.1f));
            if (parent.frameCount%speed==1){
                randomPoint = Vec3D.randomVector().normalizeTo(5000f);
                turnOnClosest(randomPoint);
            }
        }

//        if (showUnder){
//            blocks.forEach(Block::displayDots);
//            highlight(which);
//        }

        buffer.beginDraw();
        buffer.clear();
        blocks.forEach(b -> b.display(buffer));
        buffer.endDraw();
    }

    @Override
    public void reset() {
        blocks.parallelStream().forEach(b -> b.setUsed(false));

    }

    @Override
    public void shuffle() {

    }

    @Override
    public void jitter() {
        blocks.forEach(b -> b.jitter(20f));
    }

    @Override
    public void mode(int which) {

    }

    @Override
    public void setBGColour(int colour) {

    }

    @Override
    public boolean isPlaying(){
        return moving;
    }


    public void makePyramids(){
        ArrayList <Block> tempBlocks;

        tempBlocks= new ArrayList<Block>();
        tempBlocks.add(new Block(parent,new Vec3D(),10, SIZE, tempBlocks));
        this.blocks.addAll(tempBlocks);

        tempBlocks= new ArrayList<Block>();
        tempBlocks.add(new Block(parent, new Vec3D(-1250,SIZE.y*4,-1250), 6, SIZE, tempBlocks));
        this.blocks.addAll(tempBlocks);

        tempBlocks= new ArrayList<Block>();
        tempBlocks.add(new Block(parent, new Vec3D(-2000,SIZE.y*7,-2000), 3, SIZE, tempBlocks));
        this.blocks.addAll(tempBlocks);

    }

//    public void highlight(int i){
//        Block b = blocks.get(i);
//        parent.pushStyle();
//        parent.stroke(255, 0, 0);
//        parent.strokeWeight(10f);
//        parent.point(b.x, b.y, b.z);
//        parent.stroke(0, 255, 0);
//        parent.strokeWeight(10f);
//        b.getUnderneath().forEach(k -> parent.point(k.x, k.y, k.z));
////        for (Block k:b.getUnderneath()){
////            point (k.x,k.y,k.z);
////        }
//        parent.popStyle();
//    }

    public void turnOnClosest(Vec3D reference){
        final Comparator<Block> comp = (b1, b2) -> Float.compare(b1.distanceToSquared(reference), b2.distanceToSquared(reference));
        List<Block> turnedOff =  blocks.stream().filter(b -> !b.isUsed()).collect(Collectors.toList());
        if (!turnedOff.isEmpty()) {
            Block closest = turnedOff.stream().min(comp).get();
            List<Block> under = closest.getUnderneath().stream().filter(b -> !b.isUsed()).collect(Collectors.toList());
            while (under.size() > 0) {
                closest = under.stream().max(comp).get();
                under = closest.getUnderneath().stream().filter(k -> !k.isUsed()).collect(Collectors.toList());
            }
            closest.setUsed(true);
        }
    }

    private PShape makeShape(Vec3D size, int strokeColour, int fillColour, float strokeWeight){
        PShape completeShape = parent.createShape(PConstants.GROUP);
        PShape a;

        a = parent.createShape();
        a.beginShape();
        a.stroke(strokeColour);
        a.strokeWeight(strokeWeight);
        a.fill(fillColour);
        a.vertex(0 - size.x / 2, 0 - size.y / 2, 0 - size.z / 2);
        a.vertex(0 - size.x / 2, 0 + size.y / 2, 0 - size.z / 2);
        a.vertex(0 + size.x / 2, 0 + size.y / 2, 0 - size.z / 2);
        a.vertex(0 + size.x / 2, 0 - size.y / 2, 0 - size.z / 2);
        a.endShape(PConstants.CLOSE);
        completeShape.addChild(a);

        a = parent.createShape();
        a.beginShape();
        a.stroke(strokeColour);
        a.strokeWeight(strokeWeight);
        a.fill(fillColour);
        a.vertex(0 - size.x / 2, 0 - size.y / 2, 0 + size.z / 2);
        a.vertex(0 - size.x / 2, 0 + size.y / 2, 0 + size.z / 2);
        a.vertex(0 + size.x / 2, 0 + size.y / 2, 0 + size.z / 2);
        a.vertex(0 + size.x / 2, 0 - size.y / 2, 0 + size.z / 2);
        a.endShape(PConstants.CLOSE);
        completeShape.addChild(a);

        a = parent.createShape();
        a.beginShape();
        a.stroke(strokeColour);
        a.strokeWeight(strokeWeight);
        a.fill(fillColour);
        a.vertex(0 - size.x / 2, 0 - size.y / 2, 0 - size.z / 2);
        a.vertex(0 - size.x / 2, 0 + size.y / 2, 0 - size.z / 2);
        a.vertex(0 - size.x / 2, 0 + size.y / 2, 0 + size.z / 2);
        a.vertex(0 - size.x / 2, 0 - size.y / 2, 0 + size.z / 2);
        a.endShape(PConstants.CLOSE);
        completeShape.addChild(a);

        a = parent.createShape();
        a.beginShape();
        a.stroke(strokeColour);
        a.strokeWeight(strokeWeight);
        a.fill(fillColour);
        a.vertex(0 + size.x / 2, 0 + size.y / 2, 0 - size.z / 2);
        a.vertex(0 + size.x / 2, 0 - size.y / 2, 0 - size.z / 2);
        a.vertex(0 + size.x / 2, 0 - size.y / 2, 0 + size.z / 2);
        a.vertex(0 + size.x / 2, 0 + size.y / 2, 0 + size.z / 2);
        a.endShape(PConstants.CLOSE);
        completeShape.addChild(a);

        a = parent.createShape();
        a.beginShape();
        a.stroke(strokeColour);
        a.strokeWeight(strokeWeight);
        a.fill(fillColour);
        a.vertex(0 - size.x / 2, 0 + size.y / 2, 0 - size.z / 2);
        a.vertex(0 + size.x / 2, 0 + size.y / 2, 0 - size.z / 2);
        a.vertex(0 + size.x / 2, 0 + size.y / 2, 0 + size.z / 2);
        a.vertex(0 - size.x / 2, 0 + size.y / 2, 0 + size.z / 2);
        a.endShape(PConstants.CLOSE);
        completeShape.addChild(a);

        a = parent.createShape();
        a.beginShape();
        a.stroke(strokeColour);
        a.strokeWeight(strokeWeight);
        a.fill(fillColour);
        a.vertex(0 - size.x / 2, 0 - size.y / 2, 0 - size.z / 2);
        a.vertex(0 + size.x / 2, 0 - size.y / 2, 0 - size.z / 2);
        a.vertex(0 + size.x / 2, 0 - size.y / 2, 0 + size.z / 2);
        a.vertex(0 - size.x / 2, 0 - size.y / 2, 0 + size.z / 2);
        a.endShape(PConstants.CLOSE);
        completeShape.addChild(a);

        return completeShape;
    }

    @Override
    public int getCounter() {
        return 0;
    }

    @Override
    public PShader getShader() {
        return null;
    }
}
