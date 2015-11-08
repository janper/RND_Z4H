package sk.janper.rnd;

//import ipcapture.IPCapture;

//import org.opencv.highgui.VideoCapture;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.ReadonlyVec2D;
import toxi.geom.Vec2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.net.HttpURLConnection;


/**
 * Created by Jan on 08.08.2015.
 */
public class Mirror extends Vec2D implements ImageObserver {
//    private Capture video;
    private PApplet parent;
    private IPCapture ipVideo;
//    private VideoCapture capture;

    private DataInputStream dis;
    private BufferedImage image = null;
    private Dimension imageSize = null;
    private HttpURLConnection huc = null;


    private int stepSize = 120;

    private int thresholdMin;
    private int thresholdMax;
    private processing.opengl.PShader halftoneShader;
    public String stream = "http://admin:hesloveslo@192.168.2.100:80/video.cgi?resolution=320x240";
    private boolean connected= false;
    private boolean initCompleted = false;

    public Mirror(ReadonlyVec2D readonlyVec2D, PApplet parent, int w, int h) {
        super(readonlyVec2D);
        this.parent = parent;
        makeVideo();
        halftoneShader = parent.loadShader("halftone.glsl");
    }

    private void makeVideo() {
        ipVideo = new IPCapture(parent, stream, "", "");
        ipVideo.start();


//        try{
//            URL u = new URL (stream);
//            huc = (HttpURLConnection) u.openConnection();
//            InputStream is =huc.getInputStream();
//
//            connected = true;
//
//            BufferedInputStream bis = new BufferedInputStream(is);
//            dis = new DataInputStream(bis);
//            if (!initCompleted){
//                initDisplay();
//            }
//
//        } catch (IOException e){
//             try{
//                 huc.disconnect();
//                 Thread.sleep(60);
//             } catch (InterruptedException ie){
//                 huc.disconnect();
//                 makeVideo();
//             }
//
//        }



//        video = new Capture(parent, w, h);




//        ipVideo = new IPCapture(parent, "http://admin:hesloveslo@192.168.2.100:80/video.cgi?resolution=320x240", "", "");
//        ipVideo.start();

//        ipVideo = new IPCapture(parent);
//        ipVideo.start("http://192.168.2.100:80/video.cgi?resolution=VGA", "admin", "hesloveslo");

//        System.out.println("Video alive: "+ipVideo.isAlive());
//        System.out.println("Video available: "+ipVideo.isAvailable());


//        System.out.println("Video width: " + video.width + " height: " + video.height);
    }

    private void initDisplay() {
        readMJPGStream();
        imageSize = new Dimension (image.getWidth(this), image.getHeight(this));
    }

    private void readMJPGStream() {
        readLine(3, dis);
        readJPG();
        readLine(2,dis);
    }

    public void disconnect() {
        try {
            if (connected) {
                dis.close();
                connected = false;
            }
        } catch (Exception e) {
        }
    }

    private void readJPG() {
        try{
            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(dis);
            image = decoder.decodeAsBufferedImage();

        } catch (Exception e) {
            e.printStackTrace();
            disconnect();
        }
    }

    private void readLine(int n, DataInputStream dis) {
        for (int i=0; i<n; i++){
            readLine(dis);
        }
    }

    private void readLine(DataInputStream dis) {

        try {
            boolean end = false;
            String lineEnd = "\n";
            byte[] lineEndBytes = lineEnd.getBytes();
            byte[] byteBuf = new byte[lineEndBytes.length];

            while(!end){
                dis.read(byteBuf, 0, lineEndBytes.length);
                String t= new String(byteBuf);
//                System.out.print(t);
                if (t.equals(lineEnd)) end = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public int getWidth() {
        return (int)Math.floor(ipVideo.width/ stepSize)* stepSize;
//        return 0;
    }

//    public void setWidth(int w) {
//        this.w = w;
//        makeVideo(parent, w, h);
//    }

    public int getHeight() {
        return (int)Math.floor(ipVideo.height/ stepSize)* stepSize;
//        return 0;
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
        ipVideo.start();
    }

    public void stop(){
        ipVideo.stop();
    }

    public PImage getImage(){

//        PImage output = parent.createImage(getWidth(), getHeight(), PConstants.ARGB);
//        if (ipVideo.isAvailable()) {
//            ipVideo.read();
//            ipVideo.loadPixels();
//            output.loadPixels();
//            float pixelBrightness;
//
//            for (int y=0; y<getHeight(); y++){
//                for (int x=0; x<getWidth(); x++){
//                    int i = y*ipVideo.width+x;
//                    int j = y*getWidth()+x;
//                    pixelBrightness = parent.brightness(ipVideo.pixels[i]);
//                    if (pixelBrightness > thresholdMin && pixelBrightness < thresholdMax) {
//                        output.pixels[j] = parent.color(255);
//                    }
//                    else {
//                        output.pixels[j] = parent.color(0,0);
//                    }
//                }
//            }
//
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
//            output.updatePixels();
//        }
//        return output;
    return null;

    }

    public void display(PGraphics buffer){
        buffer.image(getImage(), x, y);
    }

    public void displayReal(PGraphics buffer) {
        buffer.image(getRealImage(), x, y, getWidth(), getHeight());
    }

    private PImage getRealImage() {
        PImage output = null;
        if (ipVideo.isAvailable()) {
            ipVideo.read();
            output = ipVideo.get(0,0,getWidth(), getHeight());
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

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }
}
