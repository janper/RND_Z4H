package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.Vec2D;

/**
 * Created by rndzvuk on 2.11.2015.
 */
public class FlakeBuffer {
    private boolean[] buffer;
    private float scale;
    private int w;
    private int h;

    public FlakeBuffer(int width, int height, float scale) {
        this.scale = scale;
        w = Math.round(width*scale);
        h=Math.round(height*scale);
        buffer = new boolean[w * h];
        reset();
    }

    public boolean get (float x, float y){
        int scaledX = Math.round(x*scale);
        int scaledY = Math.round(y*scale);
        int i = ((int) (Math.floor(scaledY / w) + scaledX));
        if (i<0) i=0;
        if (i>buffer.length-1) i= buffer.length-1;
        return buffer[i];
    }

    public boolean get (Vec2D position){
        return this.get(position.x, position.y);
    }

    public void set (float x, float y, boolean value){
        int scaledX = Math.round(x*scale);
        int scaledY = Math.round(y*scale);
        int i = ((int) (Math.floor(scaledY / w) + scaledX));
        if (i<0) i=0;
        if (i>buffer.length-1) i= buffer.length-1;
        buffer[i] = value;
    }

    public void set (Vec2D position, boolean value){
        this.set (position.x, position.y, value);
    }

    public void reset (){
        for (int y=0; y<h; y++){
            for (int x=0; x<w; x++){
                int i = y*w+x;
                buffer[i] = x == 0 || x == w - 1 || y == h - 1;
            }
        }
    }

    public boolean getLeft(float x, float y) {
        return get (x-1/scale, y);
    }

    public boolean getRight(float x, float y) {
        return get (x+1/scale, y);
    }

    public boolean getLeft(Vec2D position) {
        return getLeft(position.x, position.y);
    }

    public boolean getRight(Vec2D position) {
        return getRight(position.x, position.y);
    }

    public void display (PApplet parent, PGraphics canvas){
        canvas.beginDraw();
        canvas.stroke (parent.color(255, 0, 0));
        canvas.strokeWeight (2f);

        for (int y=0; y<h; y++){
            for (int x=0; x<w; x++){
                int i = y*w+x;
                if (buffer[i]){
                    canvas.point(x/scale,y/scale);
                }
            }
        }
        canvas.endDraw();
    }
}
