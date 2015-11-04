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
    private float lineWidth = 1f;

    private float itemSize = 50f;

    private PImage outImg;
    private PGraphics pattern;
    private boolean smooth = true;
    private boolean dots = true;

    public Tapeta(PApplet parent) {
        super();
        this.parent = parent;
        this.randomizeAngles();
        setItemSize(itemSize);
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

    public void drawPolyLineDirect (ArrayList<Vec2D> points){
        if (dots) {
            float inBetween = 2f;
            parent.beginShape(PApplet.POINTS);
            parent.stroke (parent.color(12, 52, 173));
            parent.strokeWeight(2f);
            for (int i = 0; i < points.size()-1; i++) {
                Vec2D first = points.get(i);
                Vec2D second = points.get(i+1);
                for (int j = 0; j<inBetween; j++){
                    Vec2D current = first.interpolateTo(second,inBetween/j);
                    parent.vertex(current.x, current.y);
                }
            }
            parent.endShape();
        } else {
            parent.beginShape();
            if (smooth) {
                points.forEach(p -> parent.curveVertex(p.x, p.y));
            } else {
                points.forEach(p -> parent.vertex(p.x, p.y));
            }
            parent.endShape();
        }
    }

//    public PImage drawWallpaper (int xCount, int yCount){
////        pg = parent.createGraphics(parent.width, parent.height);
////        pg.smooth();
////        pg.clear();
////        pg.background(32);
//
//        for (int y = 0; y<outImg.height; y++){
//            for (int x = 0; x<outImg.width; x++){
//                outImg.set(x, y, parent.color(0f,0f,0f,0f));
//            }
//        }
//
//        pattern = parent.createGraphics((int)itemSize, (int)itemSize);
//        pattern.smooth();
//
//        pattern.stroke(lineColor);
//        pattern.strokeWeight(lineWidth);
//        pattern.noFill();
//        pattern.translate(itemSize / 2, itemSize / 2);
//
//        for (int step = 0; step < axes; step++) {
//            float angle = (float) Math.PI * 2 / axes * step;
//            this.setRotation(angle);
//            this.setReflect(false);
//            drawPolyLine(pattern, this.getCurrentPointPositions());
//            this.setReflect(true);
//            drawPolyLine(pattern, this.getCurrentPointPositions());
//        }
//
//        PImage img = pattern.get();
//
//        float xStep = (pg.width-itemSize)/(xCount-1);
//        float yStep = (pg.height-itemSize)/(yCount-1);
//
//        for (int y=0; y<yCount; y++){
//            for (int x=0; x<xCount+y%2; x++){
//                float currentX = x*xStep-y%2*xStep/2;
//                float currentY = y*yStep;
//                outImg.copy(img, 0,0,(int)img.width, (int)img.height,(int)currentX, (int)currentY, (int)img.width, (int)img.height);
//            }
//        }
//        return outImg;
//    }

    public void drawWallpaper (int xCount, int yCount, PGraphics buffer){
        pattern = parent.createGraphics((int)itemSize, (int)itemSize);
        pattern.beginDraw();

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

        pattern.endDraw();

        float xStep = (buffer.width-itemSize)/(xCount-1);
        float yStep = (buffer.height-itemSize)/(yCount-1);

        for (int y=0; y<yCount; y++){
            for (int x=0; x<xCount+y%2; x++){
                float currentX = x*xStep-y%2*xStep/2;
                float currentY = y*yStep;
                buffer.image(pattern.get(), currentX, currentY);
            }
        }
    }


    public void drawWallpaperBuffer (int xCount, int yCount, PGraphics buffer){
            buffer.pushStyle();
            buffer.stroke(lineColor);
            buffer.strokeWeight(lineWidth);
            buffer.noFill();
        float xStep = (buffer.width-itemSize)/(xCount-1);
        float yStep = (buffer.height-itemSize)/(yCount-1);

        for (int y=0; y<yCount; y++){
            for (int x=0; x<xCount+y%2; x++){
                buffer.pushMatrix();
                float currentX = x*xStep-y%2*xStep/2;
                float currentY = y*yStep;

                buffer.translate(currentX+itemSize/2, currentY+itemSize/2);

                for (int step = 0; step < axes; step++) {
                    float angle = (float) Math.PI * 2 / axes * step;
                    this.setRotation(angle);
                    this.setReflect(false);
                    drawPolyLine(buffer, this.getCurrentPointPositions());
                    this.setReflect(true);
                    drawPolyLine(buffer, this.getCurrentPointPositions());
                }
                buffer.popMatrix();
            }
        }
        buffer.popStyle();
    }

    public void drawWallpaperDirect (int xCount, int yCount){
        parent.pushStyle();
        parent.stroke(lineColor);
        parent.strokeWeight(lineWidth);
        parent.noFill();
        float xStep = (parent.width-itemSize)/(xCount-1);
        float yStep = (parent.height-itemSize)/(yCount-1);

        for (int y=0; y<yCount; y++){
            for (int x=0; x<xCount+y%2; x++){
                parent.pushMatrix();
                float currentX = x*xStep-y%2*xStep/2;
                float currentY = y*yStep;

                parent.translate(currentX+itemSize/2, currentY+itemSize/2);

                for (int step = 0; step < axes; step++) {
                    float angle = (float) Math.PI * 2 / axes * step;
                    this.setRotation(angle);
                    this.setReflect(false);
                    drawPolyLineDirect(this.getCurrentPointPositions());
                    this.setReflect(true);
                    drawPolyLineDirect(this.getCurrentPointPositions());
                }
                parent.popMatrix();
            }
        }
        parent.popStyle();
    }

}
