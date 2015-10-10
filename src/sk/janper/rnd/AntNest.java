package sk.janper.rnd;

import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

/**
 * Created by Jan on 6.5.2015.
 */
public class AntNest extends Vec3D {
    private float size;

    AntNest(){
        this(new Vec3D(), 100f);
    }

    AntNest (ReadonlyVec3D position, float size){
        super(position);
        this.setSize(size);
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
}
