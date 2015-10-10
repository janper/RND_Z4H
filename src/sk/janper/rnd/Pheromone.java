package sk.janper.rnd;

import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

/**
 * Created by Jan on 6.5.2015.
 */
public class Pheromone extends Vec3D {
    private float age;

    public Pheromone(ReadonlyVec3D position, float age) {
        super(position);
        this.setAge(age);
    }

    public float getAge() {
        return age;
    }

    public void setAge(float age) {
        this.age = age;
    }

    public void incAge(){
        this.age++;
    }
}
