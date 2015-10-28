package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 06.10.2015.
 */
public class ScnPrechod1 implements Scene {
    private PApplet parent;

    private ArrayList<Light> lights;
    private boolean moving = false;

    private Vec3D direction = new Vec3D( 0, 0, (float)Math.random()*5f+5f);

    private AABB limits = new AABB();

    private String name = "Prechod 1";

    public ScnPrechod1(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        limits.set(new Vec3D(parent.width / 2, parent.height / 2, 0));
        limits.setExtent(new Vec3D (3500,1000,4000));
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
        buffer.beginDraw();
        buffer.clear();
        lights.forEach(l -> {
            l.display(buffer);
        });
        buffer.endDraw();
    }

    public void start(){
        moving = true;
    }

    public void stop(){
        moving = false;
    }

    public void mode(int which){

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
        int bgColour = colour;
    }

    @Override
    public boolean isPlaying(){
        return moving;
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
            location = new Vec3D(300, parent.height / 2 + 200, parent.random(limits.getMin().z+10, limits.getMin().z+10+spread));
            motion = direction;
            imageFile =  ("predne_kruhy.svg");
        }else {
            location = new Vec3D(parent.width - 300, parent.height / 2 + 200, parent.random(limits.getMax().z-10-spread, limits.getMax().z-10));
            motion = direction.getInverted();
            imageFile =  ("zadne_kruhy.svg");
        }

        Light l = new Light(location, parent, imageFile);
        l.setRotationAngle(0);
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


}
