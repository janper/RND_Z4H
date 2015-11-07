package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;

import java.util.ArrayList;

/**
 * Created by Jan on 20.5.2015.
 */
public class Rod extends Vec3D {

    private PApplet parent;
    private Vec3D direction;
    private float length;
    private int divisions = 4;
    private float stiffness = 1f;
    private GravityBehavior gravity;

    private ArrayList<VerletParticle> particles;
    private ArrayList<VerletSpring> springs;
    private int colour =255;
    private float weight = 2f;

    private float motion;

    private Vec3D stablePosition0, stablePosition1;
    private int counter = 0;
    private int bgColour = 0;

    public Rod(PApplet parent, ReadonlyVec3D readonlyVec3D, Vec3D direction, GravityBehavior gravity) {
        super(readonlyVec3D);
        this.direction = direction;
        this.gravity = gravity;
        this.length = direction.magnitude();
        this.parent = parent;
        this.motion = parent.random(parent.height*4f, parent.height*7f);
    }

    public void setBgColour(int colour){
        bgColour = colour;
    }

    public void calculate() {
        this.particles = new ArrayList<>();
        this.springs = new ArrayList<>();

        float step = getLength() / this.getDivisions();

        for (int i = 0; i <= getDivisions(); i++) {
            Vec3D stepVector = getDirection().copy().normalizeTo(i * step/10f);
            VerletParticle particle = new VerletParticle(this.add(stepVector));
            particle.addBehavior(getGravity());
            this.particles.add(particle);
        }

        for (int i = 0; i < 2; i++) {
            particles.get(i).lock();
        }

        for (int i = 0; i < particles.size() - 1; i++) {
            VerletSpring spring = new VerletSpring(particles.get(i), particles.get(i + 1), step, this.stiffness);
            springs.add(spring);
        }

        for (int i = 0; i < particles.size() - 2; i++) {
            VerletSpring spring = new VerletSpring(particles.get(i), particles.get(i + 2), step * 2f, this.stiffness * 0.1f);
            springs.add(spring);
        }

        for (int i = 0; i < particles.size() - 3; i++) {
            VerletSpring spring = new VerletSpring(particles.get(i), particles.get(i + 3), step * 4f, this.stiffness * 0.05f);
            springs.add(spring);
        }

        stablePosition0 = this.particles.get(0).copy();
        stablePosition1 = this.particles.get(1).copy();
    }

    public Vec3D set(ReadonlyVec3D v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();

        VerletParticle p = this.particles.get(1);
        float step = getLength() / this.getDivisions();
        Vec3D stepVector = getDirection().copy().normalizeTo(step);
        p.set(this.add(stepVector));

        return this;
    }

    public void upStable(){
        stablePosition0.y-=motion;
        stablePosition1.y-=motion;
    }

    public void upCurrent(){
        this.particles.get(0).y-=motion;
        this.particles.get(1).y-=motion;
    }

    public void downStable(){
        stablePosition0.y+=motion;
        stablePosition1.y+=motion;
    }

    public void downCurrent(){
        this.particles.get(0).y+=motion;
        this.particles.get(1).y+=motion;
    }

    public void shake(float strength) {
        this.particles.get(0).jitter(strength);
        this.particles.get(1).jitter(strength);
    }

    public void stabilize(float ammount){
        this.particles.get(0).interpolateToSelf(stablePosition0, ammount);
        this.particles.get(1).interpolateToSelf(stablePosition1,ammount);
    }


    public void display(PGraphics buffer){
        buffer.pushStyle();
        buffer.noFill();
        buffer.stroke(this.colour);
        buffer.strokeWeight(this.weight);
        buffer.beginShape();
        buffer.curveVertex(this.particles.get(0).x, this.particles.get(0).y, this.particles.get(0).z); // the first control point
        this.particles.forEach(p -> buffer.curveVertex(p.x, p.y, p.z));
//        for (int i=0; i<this.particles.size(); i++){
//            parent.curveVertex(this.particles.get(i).x, this.particles.get(i).y, this.particles.get(i).z);
//        }
        int last = this.particles.size()-1;
//        parent.curveVertex(this.particles.get(last).x, this.particles.get(last).y, this.particles.get(last).z);
        buffer.vertex(this.particles.get(last).x, this.particles.get(last).y, this.particles.get(last).z);
        buffer.endShape();
        buffer.popStyle();

//        parent.pushStyle();
//        parent.noFill();
//        parent.stroke(this.colour);
//        parent.strokeWeight(this.weight / 4);
//        for (int i=0; i<this.particles.size()-2; i++){
//            parent.line(particles.get(i).x,particles.get(i).y, particles.get(i).z, particles.get(i+2).x,particles.get(i+2).y, particles.get(i+2).z);
//        }
//        parent.popStyle();
    }


    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public GravityBehavior getGravity() {
        return gravity;
    }

    public void setGravity(GravityBehavior gravity) {
        this.gravity = gravity;
    }

    public Vec3D getDirection() {
        return direction;
    }

    public void setDirection(Vec3D direction) {
        this.direction = direction;
        setLength(direction.magnitude());
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public int getDivisions() {
        return divisions;
    }

    public void setDivisions(int divisions) {
        this.divisions = (divisions<1)?1:divisions;
    }

    public float getStiffness() {
        return stiffness;
    }

    public void setStiffness(float stiffness) {
        this.stiffness = stiffness;
    }

    public ArrayList<VerletParticle> getParticles() {
        return this.particles;
    }

    public ArrayList<VerletSpring> getSprings() {
        return this.springs;
    }

    public void display() {
        counter++;
        int duration = 120;
        parent.pushStyle();
        parent.noFill();
        parent.strokeWeight(this.weight);
        parent.stroke(colour);
        int lines = particles.size() - 1;

        if (counter < duration) {

            ArrayList<Vec3D> relativePoints = new ArrayList<>();

            for (int i = 1; i <= lines; i++) {
                Vec3D v = particles.get(i).sub(particles.get(i - 1));
                relativePoints.add(v);
            }

            Vec3D currentPosition = new Vec3D(this);

            for (int i = 1; i < lines; i++) {
                Vec3D nextPosition = currentPosition.add(relativePoints.get(i).getNormalizedTo(relativePoints.get(i).magnitude() * PApplet.map(counter, 0, duration, 0f, 1f)));
                parent.line(currentPosition.x, currentPosition.y, currentPosition.z, nextPosition.x, nextPosition.y, nextPosition.z);
                currentPosition = new Vec3D(nextPosition);
            }
        } else {

            for (int i = 0; i < lines; i++) {
                parent.line(particles.get(i).x, particles.get(i).y, particles.get(i).z, particles.get(i + 1).x, particles.get(i + 1).y, particles.get(i + 1).z);
            }
        }
        parent.popStyle();
    }


    public void leave(float percent) {
        counter++;
        if (percent>0) {
            parent.pushStyle();
            parent.noFill();
            parent.strokeWeight(this.weight);
            parent.stroke(colour);
            int lines = particles.size() - 1;

            ArrayList<Vec3D> relativePoints = new ArrayList<>();

            for (int i = 1; i <= lines; i++) {
                Vec3D v = particles.get(i).sub(particles.get(i - 1));
                relativePoints.add(v);
            }

            Vec3D currentPosition = new Vec3D(this);

            for (int i = 1; i < lines; i++) {
                Vec3D nextPosition = currentPosition.add(relativePoints.get(i).getNormalizedTo(relativePoints.get(i).magnitude() * percent));
                parent.line(currentPosition.x, currentPosition.y, currentPosition.z, nextPosition.x, nextPosition.y, nextPosition.z);
                currentPosition = new Vec3D(nextPosition);
            }
            parent.popStyle();
        }
    }
}
