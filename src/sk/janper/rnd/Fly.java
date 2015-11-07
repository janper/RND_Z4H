package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;

/**
 * Created by Jan on 17.5.2015.
 */
public class Fly extends Vec3D {

    private PShape flyShape = null;
    private PApplet parent;
    private Vec3D motionVector;
    private int age = 0;
    private float minSpeed = 1f;
    private float maxSpeed = 4f;
    private int minSaccadePeriod = 60*2;
    private int maxSaccadePeriod = 60*10;
    private int currentSaccadePeriod;
    private int lastSaccadeAge = age;
    private float saccadeSlowdownMoment = 0.2f;
    private float minSaccadeAngle = (float)Math.toRadians(30d);
    private float maxSaccadeAngle = (float)Math.toRadians(100d);
    private Vec3D bowingVector;
    private Vec3D compoundVector;
    private float bowingFactor = 0.25f;
    private boolean clockwise = false;

    int color = 255;

    public Fly(PApplet parent, ReadonlyVec3D location, Vec3D motion){
        super (location);
        this.parent = parent;
        setMotionVector(motion);
        saccade();

        try {
            flyShape = parent.loadShape("mucha.svg");
        } catch(Exception e){
//            flyShape = parent.createShape();
//            flyShape.stroke(255);
            System.out.println(e);
        }
    }
    
    public Fly (PApplet parent, ReadonlyVec3D location){
        super(location);
        this.parent = parent;
        Vec3D motion = Vec3D.randomVector();
        motion.z=0;
        motion.normalizeTo(this.getMaxSpeed());
        setMotionVector(motion);
        saccade();

        flyShape = parent.loadShape("mucha.svg");
    }
    
    public void update(){
        int phase = whatPhase();
//        System.out.println(" "+phase);
        if (phase == 0){
            accelerationMove();
        }
        if (phase == 1){
            constantMove();
        }
        if (phase == 2){
            deccelerationMove();
        }
        if (phase ==3){
            saccade();
        }
        incAge();
    }

    private int whatPhase() {
        float currentPhase = (float)(getAge()-getLastSaccadeAge())/(float)getCurrentSaccadePeriod();
//        System.out.print(currentPhase);
        if (currentPhase == 1){
            return 3;
        }
        if (currentPhase <= getSaccadeSlowdownMoment()){
//            this.color = parent.color(0,0,255);
            return 0;
        }
        if ((currentPhase > getSaccadeSlowdownMoment())&&(currentPhase < (1-getSaccadeSlowdownMoment()))){
//            this.color = parent.color(0,255,0);
            return 1;
        }
        if (currentPhase > (1-getSaccadeSlowdownMoment())){
//            this.color = parent.color(255,0,0);
            return 2;
        }
        return 4;
    }


    private Vec3D getCurrentBowingVector(){
        float currentBowing = PApplet.map(getAge() - getLastSaccadeAge(), 0, getCurrentSaccadePeriod(), getBowingFactor() * getMaxSpeed(), -1 * getBowingFactor() * getMaxSpeed());
        return getBowingVector().copy().getNormalizedTo(currentBowing);
    }

    private void moveFly (float currentSpeed){
        this.compoundVector =getMotionVector().normalizeTo(currentSpeed).add(getCurrentBowingVector());
        this.addSelf(this.compoundVector);
    }

    private void constantMove() {
        moveFly(getMaxSpeed());
    }

    private void accelerationMove() {
        float currentSpeed = PApplet.map(getAge()-getLastSaccadeAge(), 0, getSaccadeSlowdownMoment()*getCurrentSaccadePeriod(),getMinSpeed(), getMaxSpeed());
        moveFly(currentSpeed);
    }

    private void deccelerationMove() {
        float currentSpeed = PApplet.map(getAge()-getLastSaccadeAge(), (1-getSaccadeSlowdownMoment())*getCurrentSaccadePeriod(), getCurrentSaccadePeriod(), getMaxSpeed(), getMinSpeed());
        moveFly(currentSpeed);
    }
    public void incAge(){
        this.age++;
    }

    public void saccade(){
        int direction = isClockwise()?1:-1;
        Vec3D saccadeVector = getMotionVector().copy().rotateZ(direction * (float) (getMinSaccadeAngle() + Math.random() * (getMaxSaccadeAngle() - getMinSaccadeAngle()))).copy().rotateX((float) (-1 * (getMaxSaccadeAngle() - getMinSaccadeAngle()) * 0.01 + Math.random() * 2 * (getMaxSaccadeAngle() - getMinSaccadeAngle()) * 0.01)).copy().rotateY((float) (-1 * (getMaxSaccadeAngle() - getMinSaccadeAngle()) * 0.01 + Math.random() * 2 * (getMaxSaccadeAngle() - getMinSaccadeAngle()) * 0.01));
//        Vec3D saccadeVector = getMotionVector().copy().rotateZ(direction * (float) (getMinSaccadeAngle() + Math.random() * (getMaxSaccadeAngle() - getMinSaccadeAngle())));
        setClockwise((Math.random() < 0.1) ? !isClockwise() : isClockwise());
        setBowingVector(saccadeVector);
        setMotionVector(saccadeVector);
        setLastSaccadeAge(getAge());
        setCurrentSaccadePeriod((int) (getMinSaccadePeriod() + Math.random() * (getMaxSaccadePeriod() - getMinSaccadePeriod())));
    }

    public float angleBetweenVectors(ReadonlyVec3D v1, ReadonlyVec3D v2){
        return (float)(Math.atan2(v1.y(), v1.x()) - Math.atan2(v2.y(), v2.x()));
    }

    public void display(PGraphics buffer){
        buffer.pushStyle();

        buffer.pushMatrix();

        float rotationAngle = angleBetweenVectors(new Vec3D(this.compoundVector.x, this.compoundVector.y, 0),Vec3D.Y_AXIS);

        buffer.translate(this.x, this.y, this.z);
        buffer.rotate(rotationAngle+(float)Math.PI);

        float flyWidth = 40f;
        float flyHeight = 40f;
        buffer.shape(this.flyShape, -0.5f * flyWidth, -0.5f * flyHeight, flyWidth, flyHeight);

        buffer.popMatrix();
        buffer.popStyle();
    }
    
    //getters and setters

    public Vec3D getMotionVector() {
        return motionVector;
    }

    public void setMotionVector(Vec3D motionVector) {
        this.motionVector = motionVector;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(float minSpeed) {
        this.minSpeed = minSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getMinSaccadePeriod() {
        return minSaccadePeriod;
    }

    public void setMinSaccadePeriod(int minSaccadePeriod) {
        this.minSaccadePeriod = minSaccadePeriod;
    }

    public int getMaxSaccadePeriod() {
        return maxSaccadePeriod;
    }

    public void setMaxSaccadePeriod(int maxSaccadePeriod) {
        this.maxSaccadePeriod = maxSaccadePeriod;
    }

    public int getCurrentSaccadePeriod() {
        return currentSaccadePeriod;
    }

    public void setCurrentSaccadePeriod(int currentSaccadePeriod) {
        this.currentSaccadePeriod = currentSaccadePeriod;
    }

    public int getLastSaccadeAge() {
        return lastSaccadeAge;
    }

    public void setLastSaccadeAge(int lastSaccadeAge) {
        this.lastSaccadeAge = lastSaccadeAge;
    }

    public float getSaccadeSlowdownMoment() {
        return saccadeSlowdownMoment;
    }

    public void setSaccadeSlowdownMoment(float saccadeSlowdownMoment) {
        this.saccadeSlowdownMoment = saccadeSlowdownMoment%0.5f;
    }

    public float getMinSaccadeAngle() {
        return minSaccadeAngle;
    }

    public void setMinSaccadeAngle(float minSaccadeAngle) {
        this.minSaccadeAngle = minSaccadeAngle;
    }

    public float getMaxSaccadeAngle() {
        return maxSaccadeAngle;
    }

    public void setMaxSaccadeAngle(float maxSaccadeAngle) {
        this.maxSaccadeAngle = maxSaccadeAngle;
    }

    public Vec3D getBowingVector() {
        return bowingVector;
    }

    public void setBowingVector(Vec3D currentSaccadeVector) {
        Vec3D rotationVector = getMotionVector().copy();
        rotationVector.crossSelf(currentSaccadeVector);
//        Vec3D rotatedVector = currentSaccadeVector.getRotatedAroundAxis(rotationVector,(float)(isClockwise()?(-1*Math.PI/2):(Math.PI/2)));
        Vec3D rotatedVector = currentSaccadeVector.getRotatedAroundAxis(rotationVector,(float)(Math.PI/2));
        this.bowingVector = rotatedVector.normalizeTo(getBowingFactor());
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    public float getBowingFactor() {
        return bowingFactor;
    }

    public void setBowingFactor(float bowingFactor) {
        this.bowingFactor = bowingFactor;
    }

    public void display() {
        parent.pushStyle();
        parent.strokeWeight (1f);

        parent.pushMatrix();

        float rotationAngle = angleBetweenVectors(new Vec3D(this.compoundVector.x, this.compoundVector.y, 0),Vec3D.Y_AXIS);

        parent.translate(this.x, this.y, this.z);
        parent.rotate(rotationAngle+(float)Math.PI);

        float flyWidth = 40f;
        float flyHeight = 40f;
        parent.shape(this.flyShape, -0.5f * flyWidth, -0.5f * flyHeight, flyWidth, flyHeight);

        parent.popMatrix();
        parent.popStyle();
    }
}
