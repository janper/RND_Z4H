package sk.janper.rnd;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.GravityBehavior;

import java.util.ArrayList;

/**
 * Created by Jan on 10.10.2015.
 */
public class ScnSpalna implements Scene {
    private final String name = "Spál?a";
    private PApplet parent;

    private VerletPhysics physics;
    private Vec3D gravityVector = new Vec3D(0f,1f,0f);
    private ArrayList<Rod> fur;

    private ArrayList<Integer> colors;

    private boolean moving = false;

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
    public void display() {
        if (moving) {
            physics.update();
            fur.forEach(r -> r.stabilize(0.1f));
        }
        fur.forEach(Rod::display);
    }

    @Override
    public void reset() {
        physics.clear();
        makeFur(80);
        makePhysics();
        rememberColors();
    }

    private void rememberColors() {
        colors = new ArrayList<Integer>();
        fur.forEach(r -> colors.add(r.getColour()));
    }

    @Override
    public void shuffle() {

    }

    @Override
    public boolean isPlaying(){
        return moving;
    }

    @Override
    public void jitter() {
        fur.forEach(r -> r.shake(10f));
    }

    @Override
    public void mode(int which) {
        if (which==1){
            fur.forEach(r -> r.setColour(parent.color(255)));
        } else {
            for (int i=0; i<fur.size(); i++) {
                fur.get(i).setColour(colors.get(i));
            }
        }
    }

    @Override
    public void setBGColour(int colour) {

    }


    private void initPhysics () {
        physics = new VerletPhysics();
    }

    private void makeFur(int number){
        fur = new ArrayList<Rod>();
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
                int topColour = parent.lerpColor(parent.color(8, 204, 135), parent.color(125, 90, 7), parent.map(x, 0, xSteps - 1, 0, 1));
                int bottomColour = parent.lerpColor(parent.color(0, 255, 255), parent.color(255, 0, 255), parent.map(x, 0, xSteps - 1, 0, 1));
                int colour = parent.lerpColor(topColour, bottomColour, parent.map(y, 0, ySteps - 1, 0, 1));
                rod.setColour(colour);
                rod.calculate();
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
}
