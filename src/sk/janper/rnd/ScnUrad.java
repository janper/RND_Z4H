package sk.janper.rnd;

import processing.core.PApplet;
import toxi.geom.Vec2D;

import java.util.ArrayList;

/**
 * Created by Jan on 08.10.2015.
 */
public class ScnUrad implements Scene {
    private PApplet parent;


    private ArrayList<TextBox> textBoxes;
    private static final int NUMBER = 20;
    private static final Vec2D GRID_STEP = new Vec2D(60,70);

    private boolean moving = false;

    private String name = "Urad";
    private int bgColour;


    public ScnUrad(PApplet parent) {
        System.out.print("Constructing "+name);
        this.parent = parent;
        reset();
        System.out.println(" done!");
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
    public void display() {
        if (moving) {
            textBoxes.forEach(tb -> tb.update());
        }
        textBoxes.forEach(tb -> parent.image(tb.get(), tb.getPosition().x, tb.getPosition().y));

    }

    @Override
    public void reset() {
        textBoxes = new ArrayList<>();
        for (int i=0; i<NUMBER; i++){
            TextBox tb = new TextBox(parent,(int)parent.random (4,15), GRID_STEP);
            randomizeTextBox(tb);
            textBoxes.add(tb);
        }
    }

    @Override
    public void shuffle() {
        textBoxes.forEach(tb -> randomizeTextBoxText(tb));
    }

    @Override
    public void jitter() {
        organize();
    }

    @Override
    public void mode(int which) {

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

    public String loremIpsum(int length){
        String output = "";
        for (int i=0; i<length; i++){
            output+=(char) parent.random(65,90);
        }
        return output;
    }

    public void randomizeTextBox(TextBox tb) {
        tb.setText(loremIpsum((int) parent.random(3, 20)));
        tb.setTargetPosition(new Vec2D(parent.random(parent.width), parent.random(parent.height)));
        tb.setTargetFillColour(parent.color(parent.random(128, 255), parent.random(128,255), parent.random(128,255)));
        tb.setTargetTextColour(parent.color(parent.random(0, 128), parent.random(0, 128), parent.random(0, 128)));
    }

    public void randomizeTextBoxText(TextBox tb) {
        tb.setText(loremIpsum((int) parent.random(3, 20)));
    }

    public void organize(){
        ArrayList<Vec2D> positions = new ArrayList<>();
        int xCells = (int)(parent.width/GRID_STEP.x);
        int yCells = (int)(parent.height/GRID_STEP.y);
        ArrayList<Integer> ignored = new ArrayList<>();

        for (int i=0; i<textBoxes.size(); i++){
            System.out.print("Organizing " + i);
            boolean fine;
            Vec2D v = new Vec2D();
            int counter = 0;
            int maxTries = 1000;
            do {
                counter++;
                fine = true;
//                System.out.print(".");
                v.x = (int)parent.random(xCells) * GRID_STEP.x;
                v.y = (int)parent.random(yCells) * GRID_STEP.y;
                if (positions.size()>0) {
                    for (int j = 0; j < i; j++) {
                        Vec2D p = positions.get(j);
                        float lowerX = p.x-(textBoxes.get(i).getCharacters()+1)*GRID_STEP.x;
                        float upperX = p.x+(textBoxes.get(j).getCharacters()+1)*GRID_STEP.x;
                        float lowerY = p.y-GRID_STEP.y*2;
                        float upperY = p.y+GRID_STEP.y*2;
                        if ((v.x>lowerX && v.x<upperX)&&(v.y>lowerY && v.y<upperY) &&(isNotInList(ignored, j))) {
                            fine = false;
                        }
                    }
                }
            }while ((!fine)&&(counter< maxTries));
            if (counter==maxTries){
                System.out.print(" skipped ");
                ignored.add(i);
            }
            System.out.println("!");
            positions.add(v);
            TextBox tb = textBoxes.get(i);
            tb.setTargetPosition(v);
            tb.setText(loremIpsum((int) parent.random(20)));
            tb.setTargetFillColour(parent.color(parent.random(128, 255), parent.random(128, 255), parent.random(128, 255)));
            tb.setTargetTextColour(parent.color(parent.random(0, 128), parent.random(0, 128), parent.random(0,128)));
        }

        ArrayList<TextBox> newTB = new ArrayList<TextBox>();
        for (int i=0; i<textBoxes.size();i++){
            if (isNotInList(ignored,i)){
                newTB.add(textBoxes.get(i));
            } else {
                System.out.println("Ignored");
            }
        }
        textBoxes = newTB;
    }

    private boolean isNotInList(ArrayList<Integer> ignored, int j) {
        boolean output = true;
        for (Integer i:ignored){
            if (i.intValue()==j){
                output = false;
            }
        }
        return output;
    }

}
