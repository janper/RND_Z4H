package sk.janper.rnd;

import ipcapture.IPCapture;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.ReadonlyVec2D;
import toxi.geom.Vec2D;

/**
 * Created by Jan on 08.08.2015.
 */
public class Mirror extends Vec2D {
//    private Capture video;
    private PApplet parent;
    private IPCapture IPVideo;

    private int stepSize = 120;

    private int thresholdMin;
    private int thresholdMax;
    private processing.opengl.PShader halftoneShader;

    public Mirror(ReadonlyVec2D readonlyVec2D, PApplet parent, int w, int h) {
        super(readonlyVec2D);
        this.parent = parent;
        makeVideo(parent, w, h);
        halftoneShader = parent.loadShader("halftone.glsl");
    }

    private void makeVideo(PApplet parent, int w, int h) {
//        video = new Capture(parent, w, h);
        IPVideo = new IPCapture(parent, "http://admin:hesloveslo@192.168.2.100:80/video.cgi?resolution=320x240", "", "");
        IPVideo.start();

//        IPVideo = new IPCapture(parent);
//        IPVideo.start("http://192.168.2.100:80/video.cgi?resolution=VGA", "admin", "hesloveslo");

        System.out.println("Video alive: "+IPVideo.isAlive());
        System.out.println("Video available: "+IPVideo.isAvailable());


//        System.out.println("Video width: " + video.width + " height: " + video.height);
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public int getWidth() {
        return (int)Math.floor(IPVideo.width/ stepSize)* stepSize;
    }

//    public void setWidth(int w) {
//        this.w = w;
//        makeVideo(parent, w, h);
//    }

    public int getHeight() {
        return (int)Math.floor(IPVideo.height/ stepSize)* stepSize;
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
//        IPVideo.start();
    }

    public void stop(){
//        IPVideo.stop();
    }

    public PImage getImage(){
        //TODO: shader

        PImage output = parent.createImage(getWidth(), getHeight(), PConstants.ARGB);
        if (IPVideo.isAvailable()) {
            IPVideo.read();
            IPVideo.loadPixels();
            output.loadPixels();
            float pixelBrightness;

            for (int y=0; y<getHeight(); y++){
                for (int x=0; x<getWidth(); x++){
                    int i = y*IPVideo.width+x;
                    int j = y*getWidth()+x;
                    pixelBrightness = parent.brightness(IPVideo.pixels[i]);
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
        if (IPVideo.isAvailable()) {
            IPVideo.read();
            output = IPVideo.get(0,0,getWidth(), getHeight());
            }
        return output;
    }

    public void displayReal() {
        PImage img = getRealImage();
        if (img!=null) {
            parent.image(getRealImage(), x, y);
        }
    }

    public void display() {
        parent.shader(halftoneShader);
        PImage img = getRealImage();
        if (img!=null) {
            parent.image(getRealImage(), x, y);
        }
        parent.resetShader();
    }
}
