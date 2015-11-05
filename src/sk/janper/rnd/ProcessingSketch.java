package sk.janper.rnd;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

import java.awt.*;
import java.util.ArrayList;

public class ProcessingSketch extends PApplet{

    private final int SCENES = 1;
    int WIDTH = 1920;
    int HEIGHT = 1080;
    private boolean record = false;
    private int[] bgColors = {color(0),color(32), color(64), color(128), color(192), color(255), color(222,177,143), color(209,175,140), color(199,172,143), color (195,171,147), color(186,171,150), color(181,172,155), color(175,173,160), color(173,176, 165), color(173,180,173), color(172,187,182), color(175,195, 193), color(175,203, 204), color(180,214, 215), color(186,226,226), color(195,237, 236)};
    private int currentBgColor = 0;
    private int cycleDir = 1;
    private ArrayList<Scene> scenes = new ArrayList<>();
    private int whichScene = 0;
    private Scene scene;
    private PShader blurShader;
    private PShader shader;
    private boolean blur = false;
    private PGraphics buffer;
    private int mode = 0;
    private int modeDirection = 1;

    private char actionChar;
    private boolean action = false;

    private OscP5 osc;
    private NetAddress broadcastLocation;
    private boolean reset  =false;
    private boolean blank = true;

    private boolean allowAudio = false;

    Minim minim;
    AudioInput in;


    public void settings(){
        GraphicsDevice devices[] = getDevices();

        if(devices.length>1){
            System.out.println("More displays detected");
//            setSize();
            fullScreen(1);
        }

        size(WIDTH, HEIGHT, P3D);
        smooth();

        int density = displayDensity();
        System.out.println("Density: "+density);
        pixelDensity(density);

        super.settings();
    }

    private GraphicsDevice[] getDevices(){
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return environment.getScreenDevices();
    }

    public boolean setSize(){
        boolean returnValue = false;
            GraphicsDevice devices[] = getDevices();

            if(devices.length>1 ){
                returnValue = true;
                WIDTH =devices[1].getDisplayMode().getWidth();
                HEIGHT= devices[1].getDisplayMode().getHeight();
                println("Adjusting animation size to "+WIDTH+"x"+HEIGHT+" b/c of 2ndary display");
            }else{
                WIDTH =devices[0].getDisplayMode().getWidth();
                HEIGHT= devices[0].getDisplayMode().getHeight();
                println("Adjusting animation size to "+WIDTH+"x"+HEIGHT+" to fit primary display");
            }
        return returnValue;
    }


    public void setup() {
        buffer = createGraphics(width, height, P3D);
        blurShader = loadShader("blur.glsl");
        noCursor();
        connect();
        setMinim();
        printInstructions();
    }

    private void setMinim() {
        System.out.print("Setting audio listener");
        minim = new Minim(this);
        in = minim.getLineIn();
        System.out.println(" done!");
    }

    private void checkAudio(){
        int val = (int)abs(max(in.left.get(0),in.right.get(0))*1000);
        System.out.println("Audio: "+val);
//        if (val>80){
//            action=true;
//            mode+=modeDirection;
//            modeDirection*=-1;
//            actionChar = 'm';
//            System.out.println("Audio: mode up");
//            return;
//        }
        if (val>60){
            action=true;
            actionChar = 'q';
            System.out.println("Audio: shuffle");
            return;
        }
        if (val>30){
            action=true;
            actionChar = 'w';
            System.out.println("Audio: jitter");
            return;
        }
    }


    private void connect() {
        osc = new OscP5(this,8000);
        broadcastLocation = new NetAddress("192.168.1.4", 8090);
    }

    public void draw (){
        if (blank){
            background (0);
        } else {
            background(bgColors[currentBgColor]);
        }

            if (frameCount < SCENES + 2) {
                loadScene();
            }

            if (frameCount == SCENES + 2) {
                progressbar(1);
                for (int i = 0; i < SCENES; i++) {
                    setScene(i);
                    scene.display(buffer);
                }
                setScene(whichScene);
            }

            if (frameCount > SCENES + 2) {

                if (allowAudio) {
                    checkAudio();
                }

                if (action) {
                    action(actionChar);
                    action = false;
                }

                if (!blank) {

                    scene.setBGColour(bgColors[currentBgColor]);

                    if (scene.isDirect()){
                        scene.display();
                    } else {
                        shader = scene.getShader();
                        if (shader != null) {
                            shader(shader);
                        }

                        scene.display(buffer);
                        image(buffer, 0, 0, width, height);

                        if (shader != null) {
                            resetShader();
                        }
                    }

                    if (blur) {
                        filter(blurShader);
                    }
                    recordIfNeeded();
                }
            }


    }

    private void loadScene() {
        switch(frameCount) {
            case 1:
                progressbar(0);
                break;
//
//            case 2:
//                scenes.add(new ScnStavebniny(this));
//                progressbar((frameCount-1) / SCENES);
//                break;

            case 12:
                scenes.add(new ScnKuchyna(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 3:
                scenes.add(new ScnPrechod1(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

//            case 4:
//                scenes.add(new ScnPrechod2(this));
//                progressbar(3f / SCENES);
//                break;

            case 5:
                scenes.add(new ScnKlenotnictvo(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 6:
                scenes.add(new ScnMuchy(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

//            case 7:
//                scenes.add(new ScnUrad(this));
//                progressbar(6f / SCENES);
//                break;

            case 8:
                scenes.add(new ScnBoh(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 9:
                scenes.add(new ScnSpalna(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 10:
                scenes.add(new ScnPyramidy(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 2:
                scenes.add(new ScnPsycholog(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 11:
                scenes.add(new ScnKruhy(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 7:
                scenes.add(new ScnMravce(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 4:
                scenes.add(new ScnUmyvarka(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;
        }
    }

    private void progressbar (float progress){
//        System.out.println("Progress: "+progress);
        pushStyle();
        fill(255, 64);
        noStroke();
        ellipse(width / 2, height / 2, height / 10, height / 10);
        stroke(255);
        strokeWeight(6);
        noFill();
        arc (width / 2, height / 2, height / 10, height / 10, 0, 2*PI*progress);
        popStyle();
    }

    public void setScene (int which){
        boolean playing = true;
        if (scene!=null){
            playing = scene.isPlaying();
            scene.stop();
            buffer.camera(width / 2f, height / 2f, (height / 2f) / tan(PI * 30f / 180f), width / 2f, height / 2f, 0, 0, 1, 0);
            camera(width / 2f, height / 2f, (height / 2f) / tan(PI * 30f / 180f), width / 2f, height / 2f, 0, 0, 1, 0);
        }
        int index = Math.abs(which % scenes.size());
        scene = scenes.get(index);
        System.out.println("Setting scene " + index + ": "+scene.getName());
        if (playing) {
            scene.start();
        }
    }


    private void recordIfNeeded() {
        if (record){
            int frame = frameCount+10000;
            saveFrame("/Users/rndzvuk/Disk Google/screenshots/"+scene.getName()+frame + ".png");
        }
    }

    private void printInstructions(){
        System.out.println("i : Display info");
        System.out.println("x : Start");
        System.out.println("z : Stop");
        System.out.println("b : Change background");
        System.out.println("o: Previous scene");
        System.out.println("p: Next scene");
        System.out.println("s : Save/record frames");
        System.out.println("r : Reset");
        System.out.println("q : Shuffle");
        System.out.println("w : Jitter");
        System.out.println("a : Blur");
        System.out.println("0-1 : Scene modes");
        System.out.println("h : Halt");
        System.out.println("k : Blank");
        System.out.println("l : Allow audio listener");

        if (blank) {
            System.out.println("Currently blank");
        }
    }

    public void keyPressed(){
        actionChar = key;
        action = true;
    }

    public void action(char key){
        switch (key){
            case 'q' : System.out.println("Shuffle");
                scene.shuffle();
                break;
            case 'w' : System.out.println("Jitter");
                scene.jitter();
                break;
            case 'r' : System.out.println("Reset");
                scene.reset();
                break;
            case 's' :
                record = !record;
                System.out.println("Recording: " + record);
                break;
            case 'b' : currentBgColor+=cycleDir;
                if (currentBgColor==bgColors.length-1 || currentBgColor==0){
                    cycleDir*=-1;
                }
                System.out.println("BgColor: "+currentBgColor);
                break;
            case 'z' : System.out.println("Stop");
                scene.stop();
                break;
            case 'x' : System.out.println("Start");
                scene.start();
                break;
            case 'i' :
                printInfo();
                break;

            case '0' : System.out.println("Scene mode 0");
                mode = 0;
                scene.mode(mode);
                break;
            case '1' : System.out.println("Scene mode 1");
                mode = 1;
                scene.mode(mode);
                break;
            case '2' : System.out.println("Scene mode 2");
                mode = 2;
                scene.mode(mode);
                break;
            case '3' : System.out.println("Scene mode 3");
                mode = 3;
                scene.mode(mode);
                break;
            case '4' : System.out.println("Scene mode 4");
                mode = 4;
                scene.mode(mode);
                break;
            case '5' : System.out.println("Scene mode 5");
                mode = 5;
                scene.mode(mode);
                break;
            case '6' : System.out.println("Scene mode 6");
                mode = 6;
                scene.mode(mode);
                break;
            case '7' : System.out.println("Scene mode 7");
                mode = 7;
                scene.mode(mode);
                break;
            case '8' : System.out.println("Scene mode 8");
                mode = 8;
                scene.mode(mode);
                break;
            case '9' : System.out.println("Scene mode 9");
                mode = 9;
                scene.mode(mode);
                break;

            case 'm':
                System.out.println("Scene mode "+mode);
                scene.mode(mode);
                break;

            case 'o' : System.out.println("Previous scene");
                whichScene--;
                setScene(whichScene);
                break;
            case 'p' : System.out.println("Next scene");
                whichScene++;
                setScene(whichScene);
                break;
            case 'a' : System.out.println("Toggle blur to "+!blur);
                blur = !blur;
                break;
            case 'h' : System.out.println("Halt!");
                osc.dispose();
                exit();
                break;
            case 'k' : System.out.println("Blank: "+!blank);
                blank = !blank;
                break;
            case 'l' : System.out.println("Allow audio listener: "+!allowAudio);
                allowAudio = !allowAudio;
                break;
        }
    }

    private void printInfo() {
        System.out.println("Current scene: " + scene.getName());
//        System.out.println("Current mode: " + scene.getMode());
        System.out.println("Current frame: " + scene.getCounter());
        System.out.println("Current frame rate: " + frameRate);
    }

    void oscEvent(OscMessage theOscMessage) {
        char k = theOscMessage.addrPattern().charAt(4);

//        String[] splits = theOscMessage.addrPattern().split("/");
//        float value = Float.valueOf(splits[splits.length-1]);
//        println("Osc from: "+k);

        switch (k){
            case 'n':
                mode--;
                mode = (mode<0)?0:mode;
                mode = (mode>9)?9:mode;
                actionChar = 'm';
                action=true;
                break;
            case 'm':
                mode++;
                mode = (mode<0)?0:mode;
                mode = (mode>9)?9:mode;
                actionChar = 'm';
                action=true;
                break;
//            case 'c':
//                whichScene = (int)value;
//                System.out.println("Scene "+whichScene);
//                setScene(whichScene);
//                break;
//            case 'd':
//                mode = (int)value;
//                scene.mode(mode);
//                System.out.println("Scene mode "+mode);
//                break;
            default:
                actionChar = k;
                action=true;
                break;
        }

    }

}
