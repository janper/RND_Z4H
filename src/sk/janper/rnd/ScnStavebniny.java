package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;

/**
 * Created by rndzvuk on 8.11.2015.
 */
public class ScnStavebniny implements Scene {
    private PApplet parent;
    private String name = "Stavebniny";

    VerletPhysics physics;
    Vec3D gravityVector = new Vec3D(0f,5f,0f);

//    private ArrayList<Ladder> ladders = new ArrayList<>();

    private int mode = 0;
    private boolean moving = true;
    private int counter = 0;

    private boolean direct = true;
    private final float LADDER_WIDTH = 150f;
    private float ladderGap = 100f;
    private final float STEP_HEIGHT = 100f;
    private final float Y_OFFSET = -30f;


    public ScnStavebniny(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        int count = (int)Math.floor(parent.width / (LADDER_WIDTH + ladderGap));
        ladderGap = (parent.width-count * LADDER_WIDTH)/(count+1)+2f;
        reset();
        System.out.println(" done!");
    }

    private void initPhysics () {
        physics = new VerletPhysics();
    }

    private void makeLadders() {
        for (float x = - LADDER_WIDTH/2-10; x<=parent.width+ LADDER_WIDTH+10; x+= LADDER_WIDTH +ladderGap){
            VerletParticle tempLadder = new Ladder(parent,new Vec3D(x,parent.height/2-Y_OFFSET,0), LADDER_WIDTH, STEP_HEIGHT);
            tempLadder.addBehavior(new GravityBehavior(gravityVector));
            physics.addParticle(tempLadder);
        }
        for (int i=0; i<physics.particles.size()-1; i++){
            VerletParticle verletParticle1 = physics.particles.get(i);
            VerletParticle verletParticle2 = physics.particles.get(i + 1);
            float dist = verletParticle1.distanceTo(verletParticle2);
            VerletSpring tempSpring = new VerletSpring(verletParticle1, verletParticle2, dist*1f, 0.5f);
            physics.addSpring(tempSpring);
        }

        physics.particles.get(0).lock();
        physics.particles.get(physics.particles.size()-1).lock();
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

    }

    @Override
    public void display() {
        counter++;
        if (mode == 0) {
            physics.update();
        }
        if (mode == 1){
            physics.particles.forEach(p -> p.y-=0.25f);
        }
        if (mode == 2){
            physics.particles.forEach(p -> p.y+=0.25f);
        }
        for (VerletParticle particle : physics.particles) {
            Ladder tempLadder = (Ladder)particle;
            tempLadder.displayDirect();
        }
    }

    @Override
    public void reset() {
        stop();
        initPhysics();
        makeLadders();
        start();
    }

    @Override
    public void shuffle() {
        int which = (int) parent.random(1, physics.particles.size() - 1);
        float value = parent.random(100, 200);
        int sign = (parent.random(1f) > 0.5f) ? 1 : -1;
        physics.particles.get(which).z+=(value * sign);
    }

    @Override
    public void jitter() {
        int count = physics.particles.size();
        int randomParticle = (int)parent.random(1, count-1);
        float value = parent.random(-25,25);
        physics.particles.get(randomParticle).y+=value;
    }

    @Override
    public void mode(int which) {
        mode = which;

    }

    @Override
    public void setBGColour(int colour) {

    }

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public boolean isPlaying() {
        return moving;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }

    @Override
    public PShader getShader() {
        return null;
    }
}
