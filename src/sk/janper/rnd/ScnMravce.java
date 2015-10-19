package sk.janper.rnd;

import processing.core.PApplet;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 08.10.2015.
 */
public class ScnMravce implements Scene {
    private PApplet parent;
    private String name = "Mravce";
    private int bgColour;

    private Pheromones pheromones;
    private ArrayList<AntNest> antNests;
    private ArrayList<FoodSource> foodSources;
    private ArrayList<Ant> ants;

    private int defaultFoodAmount = 50;
    private int mode = 0;
    private boolean moving;

    public ScnMravce(PApplet parent) {
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
        removeEmptyFoodSources();
        if (moving) {
            pheromones.update();
            updateAnts();
        }

        if (mode > 0 && mode < 3) {
        displayNests();
        }
        if (mode >1 && mode < 4) {
            displayFoodSources();
        }
        pheromones.display2();

        displayAnts();
    }

    @Override
    public void reset() {
        this.initEnvironment(3,10);
        this.addAnts(200);
    }

    @Override
    public void shuffle() {

    }

    @Override
    public void jitter() {

    }

    @Override
    public void mode(int which) {
        mode = which;
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

    private void initEnvironment(int numberNests, int numberSources){
        this.pheromones = new Pheromones(parent);
        this.pheromones.setColor(parent.color(255));
        this.pheromones.setWeight(1f);
        this.pheromones.setMaxAge(200);

        this.antNests = new ArrayList<>();
        for (int i=0; i<numberNests; i++){
            AntNest tempNest = new AntNest(new Vec3D(parent.random(parent.width * 0.33f, parent.width * 0.66f),parent.random(parent.height * 0.33f, parent.height * 0.66f),0),parent.random(50f, 75f));
            antNests.add(tempNest);
        }

        this.foodSources = new ArrayList<>();
        for (int i=0; i<numberSources; i++){
            FoodSource tempSource = new FoodSource(new Vec3D(parent.random(parent.width),parent.random(parent.height),0),parent.random(40f, 120f),defaultFoodAmount);
            foodSources.add(tempSource);
        }
    }

    private void addAnts(int number){
        this.ants = new ArrayList<>();
        for (int i=0; i<number; i++){
//            Ant tempAnt = new Ant(this,this.antNests.get((int) random(this.antNests.size())));
            Ant tempAnt = new Ant(parent,new Vec3D (parent.random(parent.width), parent.random(parent.height),0));
            tempAnt.jitter(10f, 10f, 0);
            tempAnt.setAntNests(this.antNests);
            tempAnt.setFoodSources(this.foodSources);
            tempAnt.setPheromones(this.pheromones);
            tempAnt.setJittering(0.25f);
            tempAnt.setSpeed(parent.random(1f, 3f));
            tempAnt.setSearchDistance(tempAnt.getSpeed() * 100);
            tempAnt.setState(1);
            this.ants.add(tempAnt);
        }
    }

    private void displayNests(){
        parent.pushStyle();
        parent.ellipseMode(PApplet.CENTER);
        parent.fill(128, 160, 200, 128);
        parent.noStroke();
        antNests.forEach(nest -> parent.ellipse(nest.x, nest.y, nest.getSize() * 2, nest.getSize() * 2));
        parent.popStyle();
    }

    private void displayFoodSources(){
        parent.pushStyle();
        parent.ellipseMode(PApplet.CENTER);
        parent.noStroke();
        foodSources.forEach(food -> {
            parent.fill(128, 200, 160, PApplet.map(food.getFoodAmount(), 0, this.defaultFoodAmount, 16, 255));
            parent.ellipse(food.x, food.y, food.getSize() * 2, food.getSize() * 2);
        });
        parent.popStyle();
    }

    private void removeEmptyFoodSources(){
        foodSources.removeIf(FoodSource::isEmpty);
    }

    private void updateAnts(){
        ants.forEach(Ant::update);
    }

    private void displayAnts(){
        ants.forEach(Ant::display);
    }


}
