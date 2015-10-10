package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PShape;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by Jan on 6.5.2015.
 */
public class Ant extends Vec3D{

    private PApplet parent;
    //0=nothing, 1=explore, 2=supplying
    private int state = 0;
    private float speed = 10f;
    private float jittering = 0.5f;
    private float searchDistance = this.speed*100;
    private Vec3D motionVector;
    private ArrayList<Vec3D> motionVectors = new ArrayList<>();

    private ArrayList<AntNest> antNests;
    private ArrayList<FoodSource> foodSources;
    private Pheromones pheromones;

    private PShape antShape;
    private int pheromoneDensity = 8;

    public Ant(PApplet parent){
        this(parent, new Vec3D());
    }

    public Ant(PApplet parent, Vec3D position){
        super(position);
        this.parent = parent;
        Vec3D motionVector3D = Vec3D.randomVector();
        this.motionVector = new Vec3D (motionVector3D.x,motionVector3D.y,0).normalizeTo(this.speed);
        this.antShape = this.parent.loadShape("mravec.svg");
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getJittering() {
        return jittering;
    }

    public void setJittering(float jittering) {
        this.jittering = jittering;
    }

    public ArrayList<AntNest> getAntNests() {
        return antNests;
    }

    public void setAntNests(ArrayList<AntNest> antNests) {
        this.antNests = antNests;
    }

    public ArrayList<FoodSource> getFoodSources() {
        return foodSources;
    }

    public void setFoodSources(ArrayList<FoodSource> foodSources) {
        this.foodSources = foodSources;
    }

    public float getSearchDistance() {
        return searchDistance;
    }

    public void setSearchDistance(float searchDistance) {
        this.searchDistance = searchDistance;
    }

    public Pheromones getPheromones() {
        return pheromones;
    }

    public void setPheromones(Pheromones pheromones) {
        this.pheromones = pheromones;
    }

    private void updateAverageMotionVectors(Vec3D newVector){
        this.motionVectors.add(newVector);
        if (this.motionVectors.size()>30){
            this.motionVectors.remove(0);
        }
    }

    private Vec3D calculateAverageVector(){
        Vec3D finalVector = new Vec3D();
        this.motionVectors.forEach(v -> finalVector.addSelf(v));
        return finalVector;
    }


    public void update(){
        if (this.getState()==1) {
            this.explore();
        }
        if (this.getState()==2){
            this.bringBackFood();
        }
        if (this.getState()!=0) {
            this.updateAverageMotionVectors(this.motionVector);
            this.motionVector.jitter(this.getJittering(), this.getJittering(), 0).normalizeTo(this.getSpeed());
            this.addSelf(this.motionVector);
        }
    }

    private void bringBackFood() {
        AntNest closestNest = this.findClosestNest();
        if (closestNest.distanceToSquared(this)<=closestNest.getSize()*closestNest.getSize()){
            this.dropFood();
        }else {
            this.motionVector = closestNest.sub(this).normalizeTo(this.getSpeed());
            if (parent.frameCount%this.pheromoneDensity == 0){
                this.poopPheromone();
            }
        }
    }

    private boolean pheromoneAround(Pheromone closestPheromone){
        if (closestPheromone != null) {
            if (closestPheromone.distanceToSquared(this) < this.searchDistance * this.searchDistance){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    private boolean pickingFood(FoodSource closestFood){
        if (closestFood != null) {
            if (closestFood.distanceToSquared(this) <= closestFood.getSize() * closestFood.getSize()) {
                return true;
            } else{
                return false;
            }
        } else {
            return false;
        }
    }

    private void followPheromone(Pheromone closestPheromone) {
        this.motionVector = closestPheromone.sub(this).normalizeTo(this.getSpeed());
    }

    private void explore() {
        Pheromone closestPheromone = this.pheromones.getClosestYoungPheromoneAway(this, this.findClosestNest());
        FoodSource closestFood = this.findClosestFood();

        if (this.pickingFood(closestFood)) {
            this.pickFood(closestFood);
        } else {
            if (this.pheromoneAround(closestPheromone)) {
                this.followPheromone(closestPheromone);
            } else {
                this.randomWander();
            }
        }
    }

    private void randomWander() {
        float smoothness = 64;
        this.motionVector.rotateZ(parent.random(-1 * (float) Math.PI / smoothness, (float) Math.PI / smoothness));
    }


    private AntNest findClosestNest() {
        try {
            return this.getAntNests().parallelStream().min((nest1, nest2) -> {
                if (this.distanceToSquared(nest1) <= this.distanceToSquared(nest2)) {
                    return -1;
                } else {return 1;}
            }).get();
        } catch (NullPointerException e){
            return null;
        }
    }

    private FoodSource findClosestFood() {
        try{
            return this.getFoodSources().parallelStream().min((food1, food2) -> {
                if (this.distanceToSquared(food1) - (food1.getSize() * food1.getSize()) <= this.distanceToSquared(food2) - (food2.getSize() * food2.getSize())) {
                    return -1;
                } else {
                    return 1;
                }
            }).get();
        } catch (NullPointerException e){
            return null;
        }
    }

    public void pickFood(FoodSource fs){
        fs.removeFood(1);
        this.setState(2);
    }

    public void dropFood(){
        this.setState(1);
    }

    public void poopPheromone(){
        this.pheromones.addPheromone(new Pheromone(this, 0));
    }

    public float angleBetweenVectors(ReadonlyVec3D v1, ReadonlyVec3D v2){
        return (float)(Math.atan2(v1.y(), v1.x()) - Math.atan2(v2.y(), v2.x()));
    }

    public void display(){
        parent.pushStyle();
        parent.pushMatrix();

        float rotationAngle = angleBetweenVectors(this.calculateAverageVector(),Vec3D.Y_AXIS);

        parent.translate(this.x, this.y);
        parent.rotate(rotationAngle+(float)Math.PI);

        float antWidth = 30f;
        float antHeight = 30f;
        parent.shape(this.antShape, -0.5f * antWidth, -0.5f * antHeight, antWidth, antHeight);

        parent.popMatrix();
        parent.popStyle();
    }
}
