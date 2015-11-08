package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toxi.geom.Vec2D;

import java.util.ArrayList;

/**
 * Created by Jan on 06.10.2015.
 */
public class ScnUmyvarka implements Scene {
    private final int tileSize = 60;
    private final int videoWidth = tileSize*16;
    private final int videoHeight = tileSize*12;
    private PApplet parent;
    private int thresholdMin = 100;
    private int thresholdMax = 150;

    private Mirror mirror;

    private ArrayList<Tile> tiles;
    private boolean realistic = false;

    private boolean moving;

    private String name = "Umyvarka";
    private int bgColour;

    private boolean direct = true;


    public ScnUmyvarka(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        reset();
        System.out.println(" done!");
    }

    public void start(){
        mirror.start();
        moving = true;
    }

    public void stop(){
//        if (mirror!=null){
//            mirror.stop();
//        }
        moving = false;
    }

    public void reset() {
        initMirror(videoWidth, videoHeight);
        initTiles();
    }

    public void mode (int which){
        realistic = which != 0;

        System.out.println("Realistic: " + realistic);
    }

    @Override
    public boolean isPlaying(){
        return moving;
    }

    public void jitter() {
//        tiles.forEach(t -> t.jitter(10));
    }

    public void display(PGraphics buffer) {

//        buffer.beginDraw();
//        buffer.clear();
//
//        if (realistic && moving) {
//            mirror.displayReal(buffer);
//        } else {
//            mirror.display(buffer);
//        }
//
//        drawTiles(buffer);
//
//        buffer.endDraw();
        System.out.println("Drawing to a buffer");
    }

    public String getName(){
        return name;
    }

    public void setBGColour(int colour){
        bgColour = colour;
    }

    private void initMirror(int w, int h) {
        if (mirror!=null){
            mirror.stop();
        }

        mirror = new Mirror(new Vec2D((int) parent.random((parent.width / tileSize) - (w / tileSize)) * tileSize, (int) parent.random((parent.height / tileSize) - (h / tileSize)) * tileSize), parent, w, h);

        mirror.start();
        mirror.setThresholdMin(thresholdMin);
        mirror.setThresholdMax(thresholdMax);
        mirror.setStepSize(tileSize);
    }

    private void initTiles(){
        tiles = new ArrayList<>();
        for (int y = 0; y<parent.height; y+= tileSize) {
            for (int x = 0; x < parent.width; x += tileSize) {
                Tile tempTile = new Tile (new Vec2D(x,y), tileSize, tileSize, parent);
//                if ((x<mirror.x || x>=(mirror.x+mirror.getWidth()))||((y<mirror.y || y>= (mirror.y+mirror.getHeight())))) {
                    tiles.add(tempTile);
//                }
            }
        }
    }

    private void drawTiles(PGraphics buffer){
        tiles.forEach(t -> {
            t.update();
            t.display(buffer);
        });
    }

    public void shuffle(){
        int x = Math.round(parent.random(-1.25f, 1.25f));
        int y = Math.round(parent.random(-1.25f, 1.25f));

        System.out.println("randomX:" + x + " randomY:" + y);
        if ((mirror.x+x*tileSize>=0&&mirror.x+x*tileSize<=parent.width-videoWidth)&&
                (mirror.y+y*tileSize>=0&&mirror.y+y*tileSize<=parent.height-videoHeight)){
            System.out.println ("fit");
            mirror.x+=x*tileSize;
            mirror.y+=y*tileSize;
//            initTiles();
        }
    }

    public void decreaseThresholdMin() {
        thresholdMin--;
        mirror.setThresholdMin(thresholdMin);
        System.out.println("ThresholdMin: " + thresholdMin);
        System.out.println("ThresholdMax: " + thresholdMax);
    }

    public void increaseThresholdMin() {
        thresholdMin++;
        mirror.setThresholdMin(thresholdMin);
        System.out.println("ThresholdMin: " + thresholdMin);
        System.out.println("ThresholdMax: " + thresholdMax);
    }

    public void decreaseThresholdMax() {
        thresholdMax--;
        mirror.setThresholdMax(thresholdMax);
        System.out.println("ThresholdMin: " + thresholdMin);
        System.out.println("ThresholdMax: " + thresholdMax);
    }

    public void increaseThresholdMax() {
        thresholdMax++;
        mirror.setThresholdMax(thresholdMax);
        System.out.println("ThresholdMin: " + thresholdMin);
        System.out.println("ThresholdMax: " + thresholdMax);
    }

    public void toggleRealistic() {
        realistic = !realistic;
        System.out.println("Realistic: " + realistic);
    }

    @Override
    public int getCounter() {
        return 0;
    }

    @Override
    public PShader getShader() {
        return null;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }

    @Override
    public void display() {
        drawTiles();
        if (realistic && moving) {
            mirror.displayReal(videoWidth, videoHeight);
        } else {
            parent.pushStyle();
            parent.fill(0);
            parent.noStroke();
            parent.rect(mirror.x, mirror.y, videoWidth, videoHeight);
            parent.popStyle();
            mirror.display(videoWidth, videoHeight);
        }
    }

    private void drawTiles() {
        tiles.forEach(t -> {
            t.update();
            t.display();
        });
    }
}
