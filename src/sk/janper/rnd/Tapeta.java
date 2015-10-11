package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Vec2D;

import java.util.ArrayList;

/**
 * Created by Jan on 29.6.2015.
 */
public class Tapeta extends PolyCurve {

//    private PolyCurve this = new PolyCurve();

    private int axes = 5;
    private boolean linear = true;
    private boolean autoRandomize = true;

    private PApplet parent;
    private int lineColor = 255;
    private float lineWidth = 3f;

    private float itemSize = 50f;

    private PGraphics pg;
    private PImage outImg;
    private PGraphics pattern;
    private boolean smooth = true;

    public Tapeta(PApplet parent) {
        super();
        this.parent = parent;
        this.randomizeAngles();
        setItemSize(itemSize);
        pg = parent.createGraphics(parent.width, parent.height);
        pg.smooth();
//        outImg = parent.createImage((int)parent.width, (int)parent.height);
        outImg = pg.get();
    }

    public int getAxes() {
        return axes;
    }

    public void setAxes(int axes) {
        this.axes = axes;
    }

    public boolean isLinear() {
        return linear;
    }

    public void setLinear(boolean linear) {
        this.linear = linear;
    }

    public boolean isAutoRandomize() {
        return autoRandomize;
    }

    public void setAutoRandomize(boolean autoRandomize) {
        this.autoRandomize = autoRandomize;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public float getItemSize() {
        return itemSize;
    }

    public void setItemSize(float itemSize) {
        this.itemSize = itemSize;
        setSizes(getSegments(), (itemSize / (getSegments() + 1)) / 2);
    }

    public void setSegments(int segments) {
        setSizes(segments, (itemSize / (segments + 1)) / 2);
    }

    private void setSizes(int segments, float scale) {
        super.setSegments(segments);
        this.setScale(scale);
        this.setAllVectors();
    }

    public boolean isSmooth() {
        return smooth;
    }

    public void setSmooth(boolean smooth) {
        this.smooth = smooth;
    }

    public void update() {
        if (linear) {
            this.linearUpdate();
        }else {
            super.update();
        }

        if (this.isStable()&&this.autoRandomize){
            this.randomizeAngles();
        }
    }

    public void drawPolyLine (PGraphics tempPG, ArrayList<Vec2D> points){
        tempPG.beginShape();
        if (smooth){
            points.forEach( p -> tempPG.curveVertex(p.x, p.y));
        }else{
            points.forEach(p -> tempPG.vertex(p.x, p.y));
        }
        tempPG.endShape();
    }

    public PImage drawWallpaper (int xCount, int yCount){
//        pg = parent.createGraphics(parent.width, parent.height);
//        pg.smooth();
//        pg.clear();
//        pg.background(32);

        for (int y = 0; y<outImg.height; y++){
            for (int x = 0; x<outImg.width; x++){
                outImg.set(x, y, parent.color(0f,0f,0f,0f));
            }
        }

        pattern = parent.createGraphics((int)itemSize, (int)itemSize);
        pattern.smooth();

        pattern.stroke(lineColor);
        pattern.strokeWeight(lineWidth);
        pattern.noFill();
        pattern.translate(itemSize / 2, itemSize / 2);

        for (int step = 0; step < axes; step++) {
            float angle = (float) Math.PI * 2 / axes * step;
            this.setRotation(angle);
            this.setReflect(false);
            drawPolyLine(pattern, this.getCurrentPointPositions());
            this.setReflect(true);
            drawPolyLine(pattern, this.getCurrentPointPositions());
        }

        PImage img = pattern.get();

        float xStep = (pg.width-itemSize)/(xCount-1);
        float yStep = (pg.height-itemSize)/(yCount-1);

        for (int y=0; y<yCount; y++){
            for (int x=0; x<xCount+y%2; x++){
                float currentX = x*xStep-y%2*xStep/2;
                float currentY = y*yStep;
                outImg.copy(img, 0,0,(int)img.width, (int)img.height,(int)currentX, (int)currentY, (int)img.width, (int)img.height);
            }
        }
        return outImg;
    }

    public void drawWallpaperDirect (int xCount, int yCount){
        pattern = parent.createGraphics((int)itemSize, (int)itemSize);
        pattern.smooth();

        pattern.stroke(lineColor);
        pattern.strokeWeight(lineWidth);
        pattern.noFill();
        pattern.translate(itemSize / 2, itemSize / 2);

        for (int step = 0; step < axes; step++) {
            float angle = (float) Math.PI * 2 / axes * step;
            this.setRotation(angle);
            this.setReflect(false);
            drawPolyLine(pattern, this.getCurrentPointPositions());
            this.setReflect(true);
            drawPolyLine(pattern, this.getCurrentPointPositions());
        }

        float xStep = (parent.width-itemSize)/(xCount-1);
        float yStep = (parent.height-itemSize)/(yCount-1);

        for (int y=0; y<yCount; y++){
            for (int x=0; x<xCount+y%2; x++){
                float currentX = x*xStep-y%2*xStep/2;
                float currentY = y*yStep;
                parent.image(pattern, currentX, currentY);
            }
        }
    }

}
