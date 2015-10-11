package sk.janper.rnd;

import processing.core.PApplet;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 6.5.2015.
 */
public class Pheromones {
    private PApplet parent;
    private ArrayList<Pheromone> pheromones;
    private int color;
    private float weight;
    private int maxAge;
    private float minSearchDistance =20f;
    private float maxSearchDistance =50f;


    public Pheromones(PApplet parent) {
        this.parent = parent;
        this.pheromones = new ArrayList<>();
    }

    public ArrayList<Pheromone> getPheromones() {
        return pheromones;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void addPheromone(Pheromone pheromone){
        this.pheromones.add(pheromone);
    }

    public void removeOldPheromones(){
            pheromones.removeIf(p -> p.getAge()>this.maxAge);
    }

    public void update(){
        pheromones.forEach(p -> p.incAge());
        this.removeOldPheromones();
    }


    public Pheromone getClosestYoungPheromone(ReadonlyVec3D referencePosition){
        try {
            return pheromones.parallelStream().
                    filter(p -> p.getAge() <= this.getMaxAge()).min((p, h) -> {
                        if (referencePosition.distanceToSquared(p) < referencePosition.distanceToSquared(h)) {return -1;} else {return 1;}
                    }).get();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Pheromone getClosestYoungPheromoneAway(ReadonlyVec3D referencePosition, ReadonlyVec3D closestNest){
            Vec3D direction = referencePosition.sub(closestNest);
            try {
                return pheromones.parallelStream().
                        filter(p -> (p.getAge() <= this.getMaxAge())&&(p.sub(referencePosition).normalize().dot(direction) > 0)).
                        min((p, h) -> {
                            if (referencePosition.distanceToSquared(p) < referencePosition.distanceToSquared(h)) {return -1;} else {return 1;}
                        }).get();
            } catch (Exception e) {
                return null;
            }
    }

    public void display(){
        float minSearchDistanceSquared = this.minSearchDistance *this.minSearchDistance;
        float maxSearchDistanceSquared = this.maxSearchDistance *this.maxSearchDistance;
        if (!this.pheromones.isEmpty()) {
            for (int i = 0; i < this.pheromones.size()-1; i++) {
                Pheromone p = this.pheromones.get(i);
                for (int j = i+1; j<this.pheromones.size(); j++ ) {
                    Pheromone h = this.pheromones.get(j);
                    if ((h.distanceToSquared(p)>=minSearchDistanceSquared)&&(h.distanceToSquared(p)<=maxSearchDistanceSquared)) {
                        float age = (p.getAge()<h.getAge())?p.getAge():h.getAge();
                        int alpha = Math.round(PApplet.map(age, 0, this.getMaxAge(), 255, 0));
                        parent.pushStyle();
                        parent.stroke(this.getColor(),alpha);
                        parent.strokeWeight(this.getWeight());
                        parent.line(p.x, p.y, p.z, h.x, h.y, h.z);
                        parent.popStyle();
                    }
                }

            }
        }
    }

    public void display2(){
        try {
            pheromones.forEach(p -> {
                int alpha = Math.round(PApplet.map(p.getAge(), 0, this.getMaxAge(), 255, 0));
                parent.pushStyle();
                parent.stroke(this.getColor(),alpha);
                parent.strokeWeight(this.getWeight());
                parent.point(p.x,p.y,p.z);
                parent.popStyle();
            });
        } catch (NullPointerException e)
        {

        }
    }

}
