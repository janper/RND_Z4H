package sk.janper.rnd;

import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

/**
 * Created by Jan on 6.5.2015.
 */
public class FoodSource extends Vec3D {
    private float size;
    private int foodAmount;
    private float proportion;

    public FoodSource(ReadonlyVec3D position) {
        this(position, 50f, 25);
    }

    public FoodSource(ReadonlyVec3D position, float size, int foodAmount) {
        super(position);
        this.setSize(size);
        this.setFoodAmount(foodAmount);
        this.proportion=this.getSize()/this.getFoodAmount();
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(int foodAmount) {
        this.foodAmount = foodAmount;
    }

    public void removeFood(int amount){
        int finalFoodAmount = (amount<this.getFoodAmount())?this.getFoodAmount()-amount:0;
        this.setFoodAmount(finalFoodAmount);
        float newSize = this.getSize()-this.proportion;
        this.setSize(newSize);
    }

    public boolean isEmpty(){
        return (this.getFoodAmount()<=0);
    }
}
