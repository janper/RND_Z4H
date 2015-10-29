package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 08.10.2015.
 */
public class ScnBoh implements Scene {
    private PApplet parent;
    private String name = "Boh";
    private int bgColour;

    private ArrayList<GodRay> rays;

    private Vec3D tip;
    private Vec3D bottom;

    boolean moving = false;

    public ScnBoh(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        reset();
        System.out.println(" done!");
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
        buffer.beginDraw();
        buffer.clear();
        if (moving){
            rays.forEach(r -> {
                r.update();
            });
        }
        rays.forEach(r -> {
            r.display(buffer);
        });
        buffer.endDraw();
    }

    @Override
    public void reset() {
        tip = new Vec3D(parent.width + 500, -1 * parent.height / 2 * 1.5f, -1000);
        bottom = new Vec3D(parent.width / 2, parent.height - 100, -250);
        initRays(tip, bottom, 300, 19);
    }

    @Override
    public void shuffle() {
        rays.forEach(r -> r.alignTo());
    }

    @Override
    public void jitter() {
        rays.forEach(r -> r.jitterRay(10));
    }

    @Override
    public void mode(int which) {

    }

    @Override
    public String getName() {
        return name;

    }

    @Override
    public void setBGColour(int colour){
        bgColour = colour;
    }

    public void initRays (Vec3D tip, Vec3D bottom, float radius, int count){
        rays = new ArrayList<GodRay>();
        for (int i=0; i<count; i++){
            float x = (float)(bottom.x+Math.cos(2*Math.PI*i/count)*radius);
            float z = (float)(bottom.z+Math.sin(2 * Math.PI * i / count)*radius);
            Vec3D target = new Vec3D (x, bottom.y, z);
            target.jitter(10);
            GodRay tempRay = new GodRay(parent, tip, target, 40);
            tempRay.setStickDistance1(25);
            tempRay.setStickDistance2(150);
            rays.add(tempRay);
        }
    }

    @Override
    public boolean isPlaying(){
        return moving;
    }

    @Override
    public PGraphics getBack(){
        return null;
    }

    @Override
    public PGraphics getFront(){
        return null;
    }

    @Override
    public int getCounter() {
        return 0;
    }

    @Override
    public float getOpacity() {
        return 1f;
    }
}
