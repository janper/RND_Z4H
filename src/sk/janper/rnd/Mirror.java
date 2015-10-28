package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.*;
import toxi.geom.ReadonlyVec2D;
import toxi.geom.Vec2D;

/**
 * Created by Jan on 08.08.2015.
 */
public class Mirror extends Vec2D {
    private Capture video;
    private PApplet parent;


    private int stepSize = 120;

    private int thresholdMin;
    private int thresholdMax;

    public Mirror(ReadonlyVec2D readonlyVec2D, PApplet parent, int w, int h) {
        super(readonlyVec2D);
        this.parent = parent;
        makeVideo(parent, w, h);
    }

    private void makeVideo(PApplet parent, int w, int h) {
        video = new Capture(parent, w, h);
//        System.out.println("Video width: " + video.width + " height: " + video.height);
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public int getWidth() {
        return (int)Math.floor(video.width/ stepSize)* stepSize;
    }

//    public void setWidth(int w) {
//        this.w = w;
//        makeVideo(parent, w, h);
//    }

    public int getHeight() {
        return (int)Math.floor(video.height/ stepSize)* stepSize;
    }

//    public void setHeight(int h) {
//        this.h = h;
//        makeVideo(parent, w, h);
//    }

    public int getThresholdMin() {
        return thresholdMin;
    }

    public void setThresholdMin(int thresholdMin) {
        this.thresholdMin = thresholdMin;
    }

    public int getThresholdMax() {
        return thresholdMax;
    }

    public void setThresholdMax(int thresholdMax) {
        this.thresholdMax = thresholdMax;
    }

    public void start(){
        video.start();
    }

    public void stop(){
        video.stop();
    }

    public PImage getImage(){
        //TODO: shader

        PImage output = parent.createImage(getWidth(), getHeight(), parent.ARGB);
        if (video.available()) {
            video.read();
            video.loadPixels();
            output.loadPixels();
            float pixelBrightness;

            for (int y=0; y<getHeight(); y++){
                for (int x=0; x<getWidth(); x++){
                    int i = y*video.width+x;
                    int j = y*getWidth()+x;
                    pixelBrightness = parent.brightness(video.pixels[i]);
                    if (pixelBrightness > thresholdMin && pixelBrightness < thresholdMax) {
                        output.pixels[j] = parent.color(255);
                    }
                    else {
                        output.pixels[j] = parent.color(0,0);
                    }
                }
            }

//            for (int i = 0; i < video.pixels.length; i++) {
//                pixelBrightness = parent.brightness(video.pixels[i]);
////                System.out.println (i);
//                if (pixelBrightness > thresholdMin && pixelBrightness < thresholdMax) {
//                    output.pixels[i] = parent.color(255);
//                }
//                else {
//                    output.pixels[i] = parent.color(0,0);
//                }
//            }
            output.updatePixels();
        }
        return output;

    }

    public void display(PGraphics buffer){
        buffer.image(getImage(), x, y);
    }

    public void displayReal(PGraphics buffer) {
        buffer.image(getRealImage(), x, y, getWidth(), getHeight());
    }

    private PImage getRealImage() {
        PImage output = null;
        if (video.available()) {
            video.read();
            output = video.get(0,0,getWidth(), getHeight());
            }
        return output;
    }
}
