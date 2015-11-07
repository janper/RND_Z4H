package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

/**
 * Created by Jan on 08.10.2015.
 */
public class ScnKuchyna implements Scene {
    private PApplet parent;
    private String name = "Kuchyna";
    private int bgColour;

    private int xCount = 10;
    private int yCount = 8;

    private Tapeta tapeta;
    private boolean moving = false;

    private int counter;
    private boolean direct = true;

    private int mode = 0;
    private int[] colours;

//    private BufferShader bufferShader;

    public ScnKuchyna(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
//        bufferShader = new BuffKuchyna(parent);
        makeColours();
        reset();
        System.out.println(" done!");
    }

    private void makeColours(){
        colours = new int[3];
        colours[0] = parent.color(12, 52, 173);
        colours[1] = parent.color(199,172,143);
        colours[2] = parent.color(12, 52, 173);
    }

    @Override
    public void start() {
        moving = true;
    }

    @Override
    public void stop() {
        moving = false;
    }

    @Override
    public void display(PGraphics buffer) {
        if (moving) {
            counter++;
        }
            buffer.beginDraw();
            buffer.clear();

        if (moving) {
            tapeta.update();
        }
            tapeta.drawWallpaper(xCount, yCount, buffer);
            buffer.endDraw();
    }

    @Override
    public void display() {
        if (moving) {
            counter++;
            tapeta.update();
        }

        if (mode==0){
            parent.background(199,172,143);
        }
        if (mode==1){
            parent.background(0);
        }
        if (mode==2){
            parent.background(195,237, 236);
        }

        tapeta.setLineColor(colours[mode%colours.length]);


        tapeta.drawWallpaperDirect(xCount, yCount);
    }

    @Override
    public void reset() {
        tapeta = new Tapeta(parent);
        tapeta.setLineColor(colours[mode%colours.length]);
        tapeta.setLinear(true);
        tapeta.setLineWidth(4f);
        tapeta.setSegments(15);
        tapeta.setAxes(5);
        tapeta.setItemSize(150f);
        counter=0;
    }

    @Override
    public void shuffle() {

    }

    @Override
    public void jitter() {
        tapeta.randomizeAngles();
    }

    @Override
    public void mode(int which) {
//        if (which == 1){
//            tapeta.setSmooth(!tapeta.isSmooth());
//        }
//        if (which == 2){
//            tapeta.setLinear(!tapeta.isLinear());
//        }
        mode = which;

        if (which == 0){
            tapeta.setUpdateSteps(60*60);
        }

        if (which == 1){
            tapeta.setUpdateSteps(60*6);
        }

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setBGColour(int colour){
        bgColour = colour;
    }

    @Override
    public boolean isPlaying(){
        return moving;
    }

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public PShader getShader() {
//        bufferShader.setFPS((int)parent.frameRate);
//        return bufferShader.getShader(counter);
        return null;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }
}
