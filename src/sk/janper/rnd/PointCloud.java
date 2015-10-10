package sk.janper.rnd;

import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 08.08.2015.
 */
public class PointCloud extends Vec3D {
    private ArrayList<Vec3D> points;
    private Vec3D extent;

    public PointCloud(ReadonlyVec3D readonlyVec3D, Vec3D extent, int count) {
        super(readonlyVec3D);
        this.extent = extent;
        makeCloud(count);
    }

    public void makeCloud(int count){
        points = new ArrayList<Vec3D>();
        for (int i = 0; i<count; i++){
            float x = (float)Math.random()*extent.x*2-extent.x;
            float y = (float)Math.random()*extent.y*2-extent.y;
            float z = (float)Math.random()*extent.z*2-extent.z;
            Vec3D tempPoint = new Vec3D(x,y,z);
            points.add(tempPoint);
        }
    }

    public ArrayList<Vec3D> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Vec3D> points) {
        this.points = points;
    }

    public Vec3D getExtent() {
        return extent;
    }

    public void setExtent(Vec3D extent) {
        this.extent = extent;
    }
}
