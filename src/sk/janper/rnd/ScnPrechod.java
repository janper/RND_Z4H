package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 06.10.2015.
 */
public class ScnPrechod implements Scene {
    private PApplet parent;

    private ArrayList<Light> lights;
    private final int MIN_LIGHTS = 8;
    private final int MAX_LIGHTS = 30;
    private int numLights = MAX_LIGHTS;
    private final int SLOWDOWN_PERIOD = 60*60;

    private int slowdownMoment;

    private boolean moving = false;

    private Vec3D direction = new Vec3D( 0, 0, (float)Math.random()*5f+5f);

    private float speed = 1f;

    private AABB limits = new AABB();

    private String name = "Prechod";
    private int mode = 0;

    private Vec3D currentEye;
    private Vec3D targetEye;

    private Vec3D FRONTAL;
    private Vec3D SIDEWAYS;
    private final float TRANSTITION_SPEED = 0.1f;

    private int counter = 0;

    private BufferShader bufferShader;

    private boolean direct = true;

    public ScnPrechod(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        limits.set(new Vec3D(parent.width / 2, parent.height / 2, 0));
        limits.setExtent(new Vec3D (3500,1000,4500));
        reset();
        FRONTAL = new Vec3D(parent.width / 2f, 2f*parent.height / 3f, limits.z+limits.getExtent().z/1.5f);
        SIDEWAYS = new Vec3D (-parent.width , 1f*parent.height / 4f, limits.z);

        currentEye = FRONTAL.copy();
        targetEye = FRONTAL.copy();

        bufferShader = new BuffPrechod1(parent);

        System.out.println(" done!");
    }

    public void reset(){
        initLights(numLights);
        counter=0;
    }

    public void display(PGraphics buffer){
        if (moving) {
            check();
            lights.forEach(l -> l.update(speed));
            currentEye.interpolateToSelf(targetEye, TRANSTITION_SPEED);
            counter++;
        }
        buffer.beginDraw();
        buffer.clear();

        buffer.camera(currentEye.x, currentEye.y, currentEye.z, limits.x, limits.y+parent.height, limits.z-parent.height*0.5f, 0f, 1f, 0f);

        lights.forEach(l -> l.display(buffer));
        buffer.endDraw();
    }

    public void start(){
        moving = true;
    }

    public void stop(){
        moving = false;
    }

    public void mode(int which){
        System.out.println("Mode: "+which);
        if (mode!=1 && which==1){
            slowdownMoment = counter;
        }
        mode = which;
        lights.forEach(l -> l.setMode(mode));
        if (mode==0){
            targetEye = FRONTAL;
        }
        if (mode==1){
            targetEye = SIDEWAYS;
        }

        if (mode==9){
//            lights.forEach(l -> l.getMotionVector().scaleSelf(10f));
            speed*=10f;
        }

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
        lights = new ArrayList<>();
        float magnitude = limits.getExtent().magnitude()*2;
        for (int i = 0; i<number; i++) {
            lights.add(getLight(false, magnitude));
            lights.add(getLight(true, magnitude));
        }
    }

    private Light getLight(boolean head, float spread) {
        Vec3D location;
        Vec3D motion;
//        String imageFile;
        Light l;

        if (head){
            location = new Vec3D(300, parent.height / 2 + 200, parent.random(limits.getMin().z+10, limits.getMin().z+10+spread));
            motion = direction;
//            imageFile =  ("predne_kruhy.svg");
            l = new HeadLight(location, parent);
        }else {
            location = new Vec3D(parent.width - 500, parent.height / 2 + 200, parent.random(limits.getMax().z-10-spread, limits.getMax().z-10));
            motion = direction.getInverted();
//            imageFile =  ("zadne_kruhy.svg");
            l = new TailLight(location, parent);
        }

        l.setRotationAngle(0);
        l.setMotionVector(motion);
        l.setIdeal(motion.copy());
        l.setMode(mode);
        return l;
    }

    public void check(){
        ArrayList<Light> newLights = new ArrayList<>();
        lights.forEach( l -> {
            if (l.isInAABB(limits)){
                newLights.add(l);
            }
        });
        if (mode == 1) {
            numLights = (int) PApplet.map(counter, slowdownMoment, slowdownMoment + SLOWDOWN_PERIOD, MAX_LIGHTS, MIN_LIGHTS);
            numLights = (numLights < MIN_LIGHTS) ? MIN_LIGHTS : numLights;
        }
        if (mode ==9){
            numLights=0;
        }
        if (mode == 0){
            numLights = MAX_LIGHTS;
        }
        while (newLights.size()<numLights){
                newLights.add(getLight(Math.random() >= 0.5d, 500f));
        }
        lights = newLights;
    }

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public PShader getShader() {
        bufferShader.setFPS((int)parent.frameRate);
        return (mode==0)?bufferShader.getShader(counter):null;
    }

    @Override
    public void display() {
        if (moving) {
            counter++;
            check();
            lights.forEach(l -> l.update(speed));
            currentEye.interpolateToSelf(targetEye, TRANSTITION_SPEED);
        }

        parent.camera(currentEye.x, currentEye.y, currentEye.z, limits.x, limits.y+parent.height, limits.z-parent.height*0.5f, 0f, 1f, 0f);

        lights.forEach(l -> l.display());

    }

    @Override
    public boolean isDirect() {
        return direct;
    }
}
