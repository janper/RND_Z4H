package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 06.10.2015.
 */
public class ScnPrechod implements Scene {
    private PApplet parent;

    private ArrayList<Light> lights;
    private boolean moving = false;

    private String name = "Prechod";
    private int bgColour;

    public ScnPrechod(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        reset();
        System.out.println(" done!");
    }

    public void reset(){
        initLights(30);
    }

    public void display(PGraphics buffer){
        if (moving) {
            check(-9000, 560);
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

    @Override
    public boolean isPlaying(){
        return moving;
    }

    public void jitter(){
        float amount = 1;
        lights.forEach( l -> {
            Vec3D v = l.getMotionVector();
            v.x+=parent.random(-amount, amount);
//            l.setMotionVector(v);
        });
    }

    public void shuffle(){
        float amount = 4;
        lights.forEach( l -> {
            Vec3D v = l.getMotionVector();
            v.x+=parent.random(-amount, amount);
//            l.setMotionVector(v);
        });
    }

    public String getName(){
        return name;
    }

    public void setBGColour(int colour){
        bgColour = colour;
    }

    private void initLights(int number){
        lights = new ArrayList<Light>();
        for (int i = 0; i<number; i++) {
            lights.add(getLight(false));
            lights.add(getLight(true));
        }
    }

    private Light getLight(boolean head) {
        Vec3D location;
        Vec3D motion;
        String imageFile;

        if (head){
            location = new Vec3D(300, parent.height / 2 + 200, parent.random(-9000, -2000));
            motion = new Vec3D(0, 0, parent.random(5.5f, 0.5f));
            imageFile =  ("predne_kruhy.svg");
        }else {
            location = new Vec3D(parent.width - 300, parent.height / 2 + 200, parent.random(560, 9600));
            motion = new Vec3D(0, 0, parent.random(-5.5f, -0.5f));
            imageFile =  ("zadne_kruhy.svg");
        }

        Light l = new Light(location, parent, imageFile);
        l.setMotionVector(motion);
        l.setIdeal(motion.copy());
        return l;
    }

    public void check(int min, int max){
        ArrayList<Light> newLights = new ArrayList<Light>();
        lights.forEach( l -> {
            if (l.z<min && l.getMotionVector().z<0){
                newLights.add(getLight(false));
            } else {
                if (l.z>max && l.getMotionVector().z>0){
                    newLights.add(getLight(true));
                } else {
                    newLights.add(l);
                }
            }
        });
        lights = newLights;
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
