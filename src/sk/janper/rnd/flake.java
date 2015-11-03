package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.Vec2D;

/**
 * Created by rndzvuk on 2.11.2015.
 */
public class Flake extends Vec2D {
    private PApplet parent;
    private Vec2D fallDirection = new Vec2D(0f,16f);
    private PGraphics fallen;
    private boolean settled = false;

    public Flake(PApplet parent, float x, float y, PGraphics fallen) {
        super(x, y);
        this.parent = parent;
        this.fallen = fallen;
    }

    public void update (){
        boolean belowValue = false;
        for (int i=1; i<fallDirection.magnitude(); i++) {
            Vec2D below = this.add(fallDirection.getNormalizedTo(i));
            float alpha = parent.alpha(fallen.get((int) below.x, (int) below.y));
            belowValue = belowValue||(alpha != 0f);
        }
        if (belowValue){

            boolean belowLeftValue = false;
            for (int i=1; i<fallDirection.magnitude(); i++) {
                Vec2D below = this.add(fallDirection.getNormalizedTo(i));
                float alpha = parent.alpha(fallen.get((int) below.x-1, (int) below.y));
                belowLeftValue = belowLeftValue||(alpha != 0f);
            }

            boolean belowRightValue = false;
            for (int i=1; i<fallDirection.magnitude(); i++) {
                Vec2D below = this.add(fallDirection.getNormalizedTo(i));
                float alpha = parent.alpha(fallen.get((int) below.x-1, (int) below.y));
                belowRightValue = belowLeftValue||(alpha != 0f);
            }

            if (belowLeftValue && !belowRightValue){
                this.addSelf(fallDirection.getRotated((float)Math.PI/4));
            }

            if (!belowLeftValue && belowRightValue){
                this.addSelf(fallDirection.getRotated((float)Math.PI/-4));
            }

            if ((!belowLeftValue && !belowRightValue)||(belowLeftValue && belowRightValue)){
                this.settled = true;
                fallen.beginDraw();
                fallen.stroke(parent.color(255));
                fallen.strokeWeight(fallDirection.magnitude());
                fallen.point(x,y);
                fallen.endDraw();
            }
        } else {
            this.addSelf(fallDirection);
        }
    }

    public void display (PGraphics buffer){
        buffer.point(x,y);
    }

    public boolean isSettled() {
        return settled;
    }

    public Vec2D getFallDirection() {
        return fallDirection;
    }

    public void setFallDirection(Vec2D fallDirection) {
        this.fallDirection = fallDirection;
    }
}
