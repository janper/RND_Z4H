package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import toxi.geom.ReadonlyVec2D;
import toxi.geom.Vec2D;

/**
 * Created by Jan on 08.08.2015.
 */
public class Mirror extends Vec2D{
    private PApplet parent;
    private IPCapture ipVideo;
    private PImage img;

    private int stepSize = 120;

    private int thresholdMin;
    private int thresholdMax;
    private PShader halftoneShader;
    public String stream = "http://192.168.2.100:80/video.cgi?resolution=320x240";


    public Mirror(ReadonlyVec2D readonlyVec2D, PApplet parent, int w, int h) {
        super(readonlyVec2D);
        this.parent = parent;
        makeVideo();
        halftoneShader = parent.loadShader("halftone.glsl");
    }

    private void makeVideo() {
        ipVideo = new IPCapture(parent, stream, "admin", "hesloveslo");
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public int getWidth() {
        return (int)Math.floor(ipVideo.width/ stepSize)* stepSize;
    }

    public int getHeight() {
        return (int)Math.floor(ipVideo.height/ stepSize)* stepSize;
    }

    public int getThresholdMin() {
        return thresholdMin;
    }

    public void setThresholdMin(int thresholdMin) {
        this.thresholdMin = thresholdMin;
        halftoneShader.set("minThreshold", thresholdMin/255f);
    }

    public int getThresholdMax() {
        return thresholdMax;
    }

    public void setThresholdMax(int thresholdMax) {
        this.thresholdMax = thresholdMax;
        halftoneShader.set("maxThreshold", thresholdMax/255f);
    }

    public void start(){
        ipVideo.start();
    }

    public void stop(){
        ipVideo.stop();
    }

    public PImage getImage(){
        return null;
    }

    public void display(PGraphics buffer){
        buffer.image(getImage(), x, y);
    }

    public void displayReal(PGraphics buffer) {
        buffer.image(getRealImage(), x, y, getWidth(), getHeight());
    }

    private PImage getRealImage() {
//        System.out.println("Available: "+ipVideo.isAvailable());
        if (ipVideo.isAvailable()) {
            ipVideo.read();
            int[] pixels = ipVideo.getPixels();
            img = new PImage(ipVideo.getW(),ipVideo.getH());
            img.loadPixels();
            img.pixels = pixels;
            img.updatePixels();
        }
        return img;
    }

    public void displayReal(float w, float h) {
        PImage img = getRealImage();
        if (img!=null) {
            parent.image(getRealImage(), x, y, w, h);
        }
    }

    public void display(float w, float h) {
        PImage img = getRealImage();
        if (img!=null) {
            parent.shader(halftoneShader);
            parent.image(img, x, y, w, h);
            parent.resetShader();
        }
    }
}
