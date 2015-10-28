package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 06.10.2015.
 */
public class ScnPrechod2 implements Scene {
    private PApplet parent;

    private ArrayList<Light> lights;
    private boolean moving = false;

    private Vec3D direction = new Vec3D((float)Math.random()*5f+5f, 0, 0);

    private AABB limits = new AABB();

    private PImage imgA;
    private PImage imgB;
    private PImage imgC;

    private String name = "Prechod 2";
    private int mode = 0;

    private Vec3D offset = new Vec3D (0,-100, 0);

    private int bgColour;

    public ScnPrechod2(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        limits.set(new Vec3D(parent.width / 2, parent.height / 2, 0));
        limits.setExtent(new Vec3D(3500, 1000, 3500));

        imgA = parent.loadImage("prechod2_a.png");
        imgB = parent.loadImage("prechod2_b.png");
        imgC = parent.loadImage("prechod2_c.png");

        reset();
        System.out.println(" done!");
    }

    public void reset(){
        initLights(30);
    }

    public void display(PGraphics buffer){
        if (moving) {
            check();
            lights.forEach(l -> {
                l.update();
            });
        }

        if (mode==1){
            PGraphics g = parent.createGraphics(parent.width, parent.height);
            g.fill(bgColour);
            g.noStroke();
            g.rect(0, 0, parent.width, parent.height);
            g.image(imgB, 0,0);
            parent.background(g.get());
        }
        if (mode==2){

            PGraphics g = parent.createGraphics(parent.width, parent.height);
            g.fill(bgColour);
            g.noStroke();
            g.rect(0, 0, parent.width, parent.height);
            g.image(imgB, 0, 0);
            g.image(imgC, 0, 0);
            parent.background(g.get());
        }
        if (mode==3){
            parent.background(imgA);
        }

        buffer.beginDraw();
        buffer.clear();
        lights.forEach(l -> {
            l.display(buffer);
        });
        buffer.endDraw();
    }

    @Override
    public boolean isPlaying(){
        return moving;
    }

    public void start(){
        moving = true;
    }

    public void stop(){
        moving = false;
    }

    public void mode(int which){
        mode = which;
    }

    public void jitter(){
        shake(4f);
    }

    public void shuffle(){
       shake(10f);
    }


    public void shake(float amount){
        lights.forEach(l -> {
            Vec3D v = l.getIdeal();
            Vec3D shakeDirection = v.cross(Vec3D.Y_AXIS);
            shakeDirection.normalizeTo(parent.random(-amount, amount));
            l.setMotionVector(v.add(shakeDirection));
        });
    }


    public String getName(){
        return name;
    }

    public void setBGColour(int colour){
        this.bgColour = colour;
    }

    private void initLights(int number){
        lights = new ArrayList<Light>();
        float magnitude = limits.getExtent().magnitude()*2;
        for (int i = 0; i<number; i++) {
            lights.add(getLight(false, magnitude));
            lights.add(getLight(true, magnitude));
        }
    }

    private Light getLight(boolean head, float spread) {
        Vec3D location;
        Vec3D motion;
        String imageFile;

        if (head){
            location = new Vec3D(parent.random(limits.getMin().x+10, limits.getMin().x+10+spread), parent.height / 2 + 200, -1000);
            motion = direction;
            imageFile =  ("predne_kruhy.svg");
        }else {
            location = new Vec3D(parent.random(limits.getMax().x-10-spread, limits.getMax().x-10), parent.height / 2 + 200, -2000);
            motion = direction.getInverted();
            imageFile =  ("zadne_kruhy.svg");
        }

        Light l = new Light(location, parent, imageFile);
        l.setOffset(offset);
        l.setRotationAngle((float)Math.PI/2);
        l.setMotionVector(motion);
        l.setIdeal(motion.copy());
        return l;
    }

    public void check(){
        ArrayList<Light> newLights = new ArrayList<Light>();
        lights.forEach( l -> {
            if (l.isInAABB(limits)){
                newLights.add(l);
            } else{
                newLights.add(getLight(Math.random()<0.5d?false:true, 100f));
            }
        });
        lights = newLights;
    }

    @Override
    public PGraphics getBack(){
        return null;
    }

    @Override
    public PGraphics getFront(){
        return null;
    }


}
