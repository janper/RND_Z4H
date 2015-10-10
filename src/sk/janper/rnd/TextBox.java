package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Vec2D;

import java.util.ArrayList;

/**
 * Created by Jan on 25.5.2015.
 */
public class TextBox {
    private float factor = 0.025f;

    private Vec2D position = new Vec2D();
    private Vec2D targetPosition = position.copy();

    private Vec2D snapGrid;
    private int characters;
    private String text;
    private ArrayList<Integer> active = new ArrayList<>();
    private int fillColour = 255;
    private int textColour = 32;

    private int targetFillColour = fillColour;
    private int targetTextColour = textColour;

    private PApplet parent;
    private final int firstCharacter = 32;

    public TextBox(PApplet parent, int characters, Vec2D snapGrid) {
        this.setCharacters(characters);
        this.snapGrid = snapGrid;
        this.parent = parent;
    }

    public PImage get(){
        float overalWidth = snapGrid.x*characters;
        float overalHeight = snapGrid.y;
        PGraphics graphics = parent.createGraphics(Math.round(overalWidth), Math.round(overalHeight));
        graphics.beginDraw();
        graphics.background(fillColour);

        PGraphics alpha = parent.createGraphics(Math.round(overalWidth), Math.round(overalHeight));
        alpha.beginDraw();
        alpha.background(parent.alpha(fillColour));

        PFont balloon;
        balloon = parent.loadFont("BalloonBoold-120.vlw");
        graphics.textFont(balloon);
        graphics.textSize(snapGrid.y*4.5f/6);
        graphics.fill(textColour);

        for (int i=0;i<=characters;i++){
            float position = i*snapGrid.x;
            float lineLength = snapGrid.y*0.70f;
            if ((i==0)||(i==characters)) {
                lineLength = snapGrid.y;
            }
            alpha.strokeWeight(0.25f*snapGrid.x);
            alpha.stroke(0);
            alpha.line (position, snapGrid.y, position, snapGrid.y-lineLength);

            if ((i<characters)&&(i<text.length())) {
                String s = ""+ ((char) active.get(i).intValue());
                graphics.text(s, position + snapGrid.x / 6, snapGrid.y * 5.5f/6);
            }
        }

        PImage output = graphics.get();
        output.mask(alpha.get());

        return output;
    }

    public void update(){
        for (int i=0; i<characters; i++){
            if ((i<characters)&&(i<text.length())) {
                int ascii = (int) text.charAt(i);
                if (active.get(i)<ascii){
                    active.set(i, active.get(i)+1);
                }
                if (active.get(i)>ascii){
                    active.set(i, active.get(i)-1);
                }
            }
        }

        this.position.interpolateToSelf(this.targetPosition,this.factor);
        this.fillColour = parent.lerpColor(this.fillColour,this.targetFillColour,this.factor);
        this.textColour = parent.lerpColor(this.textColour,this.targetTextColour,this.factor);
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public Vec2D getPosition() {
        return position;
    }

    public void setPosition(Vec2D position) {
        this.position = position;
    }

    public Vec2D getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Vec2D targetPosition) {
        this.targetPosition = targetPosition;
    }

    public int getTargetFillColour() {
        return targetFillColour;
    }

    public void setTargetFillColour(int targetFillColour) {
        this.targetFillColour = targetFillColour;
    }

    public int getTargetTextColour() {
        return targetTextColour;
    }

    public void setTargetTextColour(int targetTextColour) {
        this.targetTextColour = targetTextColour;
    }

    public Vec2D getSnapGrid() {
        return snapGrid;
    }

    public void setSnapGrid(Vec2D snapGrid) {
        this.snapGrid = snapGrid;
    }

    public int getCharacters() {
        return characters;
    }

    public void setCharacters(int characters) {
        this.characters = characters;
        fixActive();
    }

    private void fixActive() {
        int size = active.size();
        int difference = characters - size;
        if (difference>0){
            for (int i=size; i<characters; i++){
                    active.add(firstCharacter);
            }
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        fixActive();
    }

    public ArrayList<Integer> getActive() {
        return active;
    }

    public void setActive(ArrayList<Integer> active) {
        this.active = active;
    }

    public int getFillColour() {
        return fillColour;
    }

    public void setFillColour(int fillColour) {
        this.fillColour = fillColour;
    }

    public int getTextColour() {
        return textColour;
    }

    public void setTextColour(int textColour) {
        this.textColour = textColour;
    }
}
