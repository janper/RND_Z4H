package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;

/**
 * Created by Jan on 06.10.2015.
 */
public class ScnKlenotnictvo implements Scene {
    private PApplet parent;

    VerletPhysics physics;
    Vec3D gravityVector = new Vec3D(0f,5f,0f);
    private float bounceRatio=100f;
    private boolean enablePhysics = true;

    private String name = "Klenotnictvo";
    private int bgColour;

    private int mode = 0;

    public ScnKlenotnictvo(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        reset();
        System.out.println(" done!");
    }



    public void reset(){
        initPhysics();
        addManyStrings(80f);
    }


    public void display (PGraphics buffer){
        if (enablePhysics) {
            adjustLocked(0.1f);
//            shakeLocked(0.05f);
            physics.update();
        }

        buffer.beginDraw();
        buffer.clear();
        buffer.smooth();
        physics.particles.forEach(particle ->{
            Diamond d = (Diamond) particle;
            Vec3D direction = diamondDirection(d);
            d.display(direction, buffer);
        });

        buffer.endDraw();
    }

    public void shuffle(){
        VerletParticle particle;
//            do {
        particle = physics.particles.get((int) parent.random(physics.particles.size() - 1));
//            }while(!(particle.isLocked()));
        Diamond d = (Diamond) particle;
        d.z+=1000f;
    }

    public void jitter(){
        float threshold = 0.25f;
        float value = parent.random(threshold, 1);
        if (value>=threshold) {
            for (VerletParticle particle : physics.particles) {
                Diamond d = (Diamond) particle;
                if (d.isLocked()) {
                    d.jitter(threshold*bounceRatio);
                }
            }
        }
    }

    @Override
    public boolean isPlaying(){
        return enablePhysics;
    }

    public void start(){
        enablePhysics = true;
    }

    public void stop(){
        enablePhysics = false;
    }

    public void mode(int which){
        mode = which;
    }

    public String getName(){
        return name;
    }

    public void setBGColour(int colour){
        bgColour = colour;
    }


    private void initPhysics () {
        physics = new VerletPhysics();
    }

    private void addManyStrings(float stepSize) {
        float xOffset = 50;
        float yOffset = -40;
        float xStepSize = (float)((parent.width-2*xOffset)/Math.floor((parent.width-2*xOffset)/stepSize));
        for (int x = Math.round(xOffset); x<parent.width-xOffset; x+=xStepSize) {
            Vec3D position = new Vec3D(x, yOffset, 0);
            Vec3D direction = new Vec3D(0, stepSize*(-1)*0.75f, 0);
            addString(position, direction, Math.round((parent.height/stepSize)*1.5f), stepSize);
        }
//        System.out.println("All strings added");
    }

    private void addString(Vec3D position, Vec3D direction, int num, float jitter) {
        Diamond tempParticle = new Diamond(parent, position.add(new Vec3D(0,-200,0)));
        tempParticle.setDefaultPosition(position);
        tempParticle.lock();
        for (int i = 0; i<num; i++) {
            //System.out.println("Adding diamond to "+tempParticle.x +", "+ tempParticle.y +", "+ tempParticle.z);
            Vec3D jitterDirection = direction.copy();
            jitterDirection.jitter(jitter);
            Diamond nextParticle = new Diamond(parent, tempParticle.add(jitterDirection)) ;
            VerletSpring tempSpring = new VerletSpring(tempParticle, nextParticle,direction.magnitude()*parent.random(0.8f,1.1f), 0.1f);
            tempParticle.addBehavior(new GravityBehavior(gravityVector));
            physics.addParticle(tempParticle);
            physics.addSpring(tempSpring);
            tempParticle=nextParticle;
        }
//        System.out.println("String added");
    }

    private void adjustLocked(float ratio){
        for (VerletParticle particle : physics.particles){
            Diamond d = (Diamond) particle;
            if (d.isLocked()) {
                d.adjustPosition(ratio);
            }
        }
    }

    private Vec3D diamondDirection(VerletParticle p){
        Vec3D output = new Vec3D();
        for (VerletSpring s:physics.springs){
            if (s.a==p){
                output=s.b.sub(s.a);
            }
        }
        return output;
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
}
