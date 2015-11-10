package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.GravityBehavior;

import java.util.ArrayList;

/**
 * Created by Jan on 10.10.2015.
 * mode 0 = biele a povoleny shuffle
 * mode 1 = farebne a bez shuffle
 * mode 2 = farebne a shuffle
 */
public class ScnSpalna implements Scene {
    private final String name = "Spalna";
    private PApplet parent;

    private VerletPhysics physics;
    private Vec3D gravityVector = new Vec3D(0f,1f,0f);
    private ArrayList<Rod> fur;

    private ArrayList<Integer> colors;

    private boolean moving = false;

    private boolean direct = true;
    private int mode = 1;
    private int bgColour = 0;
    private int counter =0;
    private float finishDuration = 20f;
    private int endPoint;

    public ScnSpalna(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        initPhysics();
        reset();
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
        buffer.beginDraw();
        buffer.clear();
        if (moving) {
            physics.update();
            fur.forEach(r -> r.stabilize(0.1f));
        }
        fur.forEach(r -> r.display(buffer));
        buffer.endDraw();
    }

    @Override
    public void reset() {
        physics.clear();
        makeFur(60);
        makePhysics();
        rememberColors();
        reColor(mode);
        counter=0;
    }

    private void rememberColors() {
        colors = new ArrayList<>();
        fur.forEach(r -> colors.add(r.getColour()));
    }

    @Override
    public void shuffle() {
        if (mode!=2) {
            fur.forEach(r -> r.shake(10f));
        }
    }

    @Override
    public boolean isPlaying(){
        return moving;
    }

    @Override
    public void jitter() {
        fur.forEach(r -> r.shake(0.5f));
    }

    @Override
    public void mode(int which) {
        if (mode==9 && which!=9){
            counter=0;
        }
        mode = which;
        reColorTarget(which);
        if (mode==9){
            endPoint = counter;
        }
    }

    private void reColorTarget(int which) {
        if (which==0){
            fur.forEach(r -> r.setTargetColour(parent.color(255)));
        }

        if (which==1 || which==2) {
            for (int i=0; i<fur.size(); i++) {
                fur.get(i).setTargetColour(colors.get(i));
            }
        }
    }

    private void reColor(int which) {
        if (which==0){
            fur.forEach(r -> r.setColour(parent.color(255)));
        }

        if (which==1 || which==2) {
            for (int i=0; i<fur.size(); i++) {
                fur.get(i).setColour(colors.get(i));
            }
        }
    }

    @Override
    public void setBGColour(int colour) {
        bgColour = colour;
    }


    private void initPhysics () {
        physics = new VerletPhysics();
    }

    private void makeFur(int number){
        fur = new ArrayList<>();
        int xSteps = number;
        int ySteps = number;
        float xStep = parent.width/(xSteps-1);
        float yStep = parent.height/(ySteps-1);
        for (int x=0; x<xSteps; x++){
            for (int y=0; y<ySteps; y++) {
                Vec3D position = new Vec3D(x*xStep, y*yStep, 0);
                position.jitter(50f);
                position.z=0;
                Vec3D direction = new Vec3D(0, 0, 100);
                GravityBehavior gravity = new GravityBehavior(gravityVector);
                Rod rod = new Rod(parent, position, direction, gravity);

                int topLeft = parent.color(12*1.5f, 52*1.5f, 173*1.5f);
                int topRight = parent.color(96*1.5f, 113*1.5f, 163*1.5f);
                int bottomLeft = parent.color(232*1.5f, 121*1.5f, 2*1.5f);
                int bottomRight = parent.color(232*1.5f, 18*1.5f, 2*1.5f);

//                int topLeft = parent.color(8, 204, 135);
//                int topRight = parent.color(125, 90, 7);
//                int bottomLeft = parent.color(0, 255, 255);
//                int bottomRight = parent.color(255, 0, 255);

                int topColour = parent.lerpColor(topLeft, topRight, PApplet.map(x, 0, xSteps - 1, 0, 1));
                int bottomColour = parent.lerpColor(bottomLeft, bottomRight, PApplet.map(x, 0, xSteps - 1, 0, 1));
                int colour = parent.lerpColor(topColour, bottomColour, PApplet.map(y, 0, ySteps - 1, 0, 1));
                rod.setColour(colour);
                rod.setBgColour(bgColour);
                rod.calculate();
//                rod.upCurrent();
                fur.add(rod);
            }
        }
    }

    private void makePhysics(){
        fur.forEach(r -> {
            r.getParticles().forEach(p -> physics.addParticle(p));
            r.getSprings().forEach(s -> physics.addSpring(s));
        });
    }

    @Override
    public int getCounter() {
        return 0;
    }

    @Override
    public PShader getShader() {
        return null;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }

    @Override
    public void display() {
        if (moving) {
            counter++;
            physics.update();
            fur.forEach(r -> r.stabilize(0.1f));
        }
        if (mode ==9){
            fur.forEach(r -> r.leave(PApplet.map( counter-endPoint, 0, finishDuration, 1f,0f)));
        } else {
            fur.forEach(r -> r.display());
        }
    }
}
