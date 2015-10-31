package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toxi.geom.AABB;
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
    private final String name = "Pyram�dy";
    public ArrayList <Block> blocks = new ArrayList<Block>();
    private PApplet parent;
    private int which = 0;
    private boolean showUnder = false;

    private Vec3D randomPoint;

    private boolean moving = false;
    private int speed = 7;
    private int bgColour = 0;
    private int colour;
    private float weight;
    private int counter = 0;

    private AABB boundingBox = new AABB();
    private float rotationSpeed = 60*60;
    private float shift;

    public ScnPyramidy(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        colour = parent.color(255);
        weight = 2f;
        makePyramids ();
        blocks.forEach(b -> boundingBox.includePoint(b));
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
            counter++;
            if (getCounter()%speed==1){
                randomPoint = Vec3D.randomVector().normalizeTo(5000f);
                turnOnClosest(randomPoint);
            }
            shift*=0.9;
        }

        buffer.beginDraw();
        buffer.clear();
        Vec3D diagonal = boundingBox.getExtent().rotateY(PApplet.map(counter, 0, rotationSpeed, 0, 2 * PConstants.PI));
        buffer.camera(boundingBox.x+diagonal.x*1.5f*(1-shift),boundingBox.y-diagonal.y*2f*(1-shift), boundingBox.z+diagonal.z*1.5f*(1-shift), boundingBox.x,boundingBox.y*1.5f,boundingBox.z,0f, 1f, 0f);
        blocks.forEach(b -> b.display(buffer));
        buffer.endDraw();
    }

    @Override
    public void reset() {
        blocks.parallelStream().forEach(b -> b.setUsed(false));

    }

    @Override
    public void shuffle() {
        shift = parent.random (-0.25f,0.25f);

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
        bgColour = colour;

    }

    @Override
    public boolean isPlaying(){
        return moving;
    }


    public void makePyramids(){
        ArrayList <Block> tempBlocks;

        tempBlocks= new ArrayList<Block>();
        Block tempBlock = new Block(parent, new Vec3D(), 10, SIZE, tempBlocks, colour, weight, bgColour);
        tempBlocks.add(tempBlock);
        this.blocks.addAll(tempBlocks);

        tempBlocks= new ArrayList<Block>();
        tempBlock = new Block(parent, new Vec3D(-1250, SIZE.y * 4, -1250), 6, SIZE, tempBlocks, colour, weight, bgColour);
        tempBlocks.add(tempBlock);
        this.blocks.addAll(tempBlocks);

        tempBlocks= new ArrayList<Block>();
        tempBlock = new Block(parent, new Vec3D(-2000, SIZE.y * 7, -2000), 3, SIZE, tempBlocks, colour, weight, bgColour);
        tempBlocks.add(tempBlock);
        this.blocks.addAll(tempBlocks);

    }


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

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public PShader getShader() {
        return null;
    }
}
