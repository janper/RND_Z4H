package sk.janper.rnd;

import processing.core.PApplet;
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

    private Vec3D stablePosition0, stablePosition1;

    public Rod(PApplet parent, ReadonlyVec3D readonlyVec3D, Vec3D direction, GravityBehavior gravity) {
        super(readonlyVec3D);
        this.direction = direction;
        this.gravity = gravity;
        this.length = direction.magnitude();
        this.parent = parent;
    }

    public void calculate() {
        this.particles = new ArrayList<VerletParticle>();
        this.springs = new ArrayList<VerletSpring>();

        float step = getLength() / this.getDivisions();

        for (int i = 0; i <= getDivisions(); i++) {
            Vec3D stepVector = getDirection().copy().normalizeTo(i * step);
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


    public void shake(float strength) {
        this.particles.get(0).jitter(strength);
        this.particles.get(1).jitter(strength);
    }

    public void stabilize(float ammount){
        this.particles.get(0).interpolateToSelf(stablePosition0, ammount);
        this.particles.get(1).interpolateToSelf(stablePosition1,ammount);
    }


    public void display(){
        parent.pushStyle();
        parent.noFill();
        parent.stroke(this.colour);
        parent.strokeWeight(this.weight);
        parent.beginShape();
        parent.curveVertex(this.particles.get(0).x, this.particles.get(0).y, this.particles.get(0).z); // the first control point
        this.particles.forEach(p -> parent.curveVertex(p.x, p.y, p.z));
//        for (int i=0; i<this.particles.size(); i++){
//            parent.curveVertex(this.particles.get(i).x, this.particles.get(i).y, this.particles.get(i).z);
//        }
        int last = this.particles.size()-1;
//        parent.curveVertex(this.particles.get(last).x, this.particles.get(last).y, this.particles.get(last).z);
        parent.vertex(this.particles.get(last).x, this.particles.get(last).y, this.particles.get(last).z);
        parent.endShape();
        parent.popStyle();

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
}
