package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

import java.awt.*;
import java.util.ArrayList;

public class ProcessingSketch extends PApplet{

    private final int SCENES = 14;
    int WIDTH, HEIGHT;
    private boolean record = false;
    private int[] bgColors = {color(0),color(32), color(64), color(128), color(192), color(255), color(222,177,143), color(209,175,140), color(199,172,143), color (195,171,147), color(186,171,150), color(181,172,155), color(175,173,160), color(173,176, 165), color(173,180,173), color(172,187,182), color(175,195, 193), color(175,203, 204), color(180,214, 215), color(186,226,226), color(195,237, 236)};
    private int currentBgColor = 0;
    private int cycleDir = 1;
    private ArrayList<Scene> scenes = new ArrayList<>();
    private int whichScene = 0;
    private Scene scene;
    private PShader blurShader;
    private boolean blur = false;
    private PGraphics back;
    private PGraphics front;
    private PGraphics buffer;
    private PGraphics blank;
    private PGraphics white;
    private PShader mixShader;

    public void init(){
        GraphicsDevice devices[] = getDevices();

        if((frame!=null)&&(devices.length>1)){
            frame.removeNotify();//make the frame not displayable
            frame.setResizable(false);
            frame.setUndecorated(true);
            System.out.println("frame is at "+frame.getLocation());
            frame.addNotify();
        }
        super.init();
    }

    private GraphicsDevice[] getDevices(){
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return environment.getScreenDevices();
    }

    public boolean setSize(){
        boolean returnValue = false;
            //***** figure out the display environment ****/
            GraphicsDevice devices[] = getDevices();
            //  System.out.println(Arrays.toString(devices));

            if(devices.length>1 ){ //we have a 2nd display/projector
                returnValue = true;
                //learn the true dimensions of the secondary display
                WIDTH =devices[1].getDisplayMode().getWidth();
                HEIGHT= devices[1].getDisplayMode().getHeight();
                println("Adjusting animation size to "+WIDTH+"x"+HEIGHT+" b/c of 2ndary display");
            }else{ //no 2nd screen but make it fullscreen anyway
                WIDTH =devices[0].getDisplayMode().getWidth();
                HEIGHT= devices[0].getDisplayMode().getHeight();
                println("Adjusting animation size to "+WIDTH+"x"+HEIGHT+" to fit primary display");
            }
        return returnValue;
    }


    public void setup() {
        if (setSize()) {
            size((int) (WIDTH * 1.75), (int) (HEIGHT * 1.75), P2D);
        } else {
            size(1920,1080, P2D);
        }
        blurShader = loadShader("blur.glsl");
        initBuffers();
        blank = createGraphics(width, height,P2D);
        white = createGraphics(width, height,P2D);
        white.beginDraw();
        white.background(255);
        white.endDraw();
        mixShader = loadShader("mixShader.glsl");

        printInstructions();
    }

    private void initBuffers() {
        back = createGraphics(width, height, P2D);
        buffer = createGraphics(width, height, P3D);
        buffer.smooth();
        //TODO: weird smooth
        front = createGraphics(width, height, P2D);
    }

    private void addScenes() {
        scenes.add(new ScnUmyvarka(this));
        scenes.add(new ScnPrechod1(this));
        scenes.add(new ScnPrechod2(this));
        scenes.add(new ScnKlenotnictvo(this));
        scenes.add(new ScnMuchy(this));
        scenes.add(new ScnUrad(this));
        scenes.add(new ScnBoh(this));
        scenes.add(new ScnSpalna(this));
        scenes.add(new ScnPyramidy(this));
    }

    public void draw (){
//        blank.beginDraw();
//        blank.clear();
//        blank.background(bgColors[currentBgColor]);
//        blank.endDraw();

        background(bgColors[currentBgColor]);

        if (frameCount<SCENES+2) {
            loadScene();
        }

        if (frameCount==SCENES+2){
            progressbar(1);
            for (int i = 0; i < SCENES; i++) {
                setScene(i);
                scene.display(buffer);
            }
            setScene(whichScene);
        }

        if (frameCount>SCENES+2) {
            scene.setBGColour(bgColors[currentBgColor]);

            scene.display(buffer);

            PImage b = (buffer==null)?blank.get():buffer.get();

            PShader shader = scene.getShader();

            if (shader != null) {
                shader(shader);
            }

            image(b, 0, 0, width, height);

            resetShader();

            if (blur){
                filter(blurShader);
            }

            recordIfNeeded();
        }

    }


//    public void draw (){
//
//        background(bgColors[currentBgColor]);
//
//        if (frameCount<SCENES+2) {
//            loadScene();
//        }
//
//        if (frameCount==SCENES+2){
//            progressbar(1);
//            setScene(whichScene);
//        }
//
//        if (frameCount>SCENES+2) {
//            scene.setBGColour(bgColors[currentBgColor]);
//
//            image (buffer, 0,0,width, height);
//            if (blur){
//                filter(blurShader);
//            }
//            recordIfNeeded();
//        }
//
//    }

    private void loadScene() {
        switch(frameCount) {
            case 1:
                progressbar(0);
                break;

            case 2:
                scenes.add(new ScnKuchyna(this));
                progressbar(13f / SCENES);
                break;

            case 3:
                scenes.add(new ScnPrechod1(this));
                progressbar(2f / SCENES);
                break;

            case 4:
                scenes.add(new ScnPrechod2(this));
                progressbar(3f / SCENES);
                break;

            case 5:
                scenes.add(new ScnKlenotnictvo(this));
                progressbar(4f / SCENES);
                break;

            case 6:
                scenes.add(new ScnMuchy(this));
                progressbar(5f / SCENES);
                break;

            case 7:
                scenes.add(new ScnUrad(this));
                progressbar(6f / SCENES);
                break;

            case 8:
                scenes.add(new ScnBoh(this));
                progressbar(7f / SCENES);
                break;

            case 9:
                scenes.add(new ScnSpalna(this));
                progressbar(8f / SCENES);
                break;

            case 10:
                scenes.add(new ScnPyramidy(this));
                progressbar(9f / SCENES);
                break;

            case 11:
                scenes.add(new ScnPsycholog(this));
                progressbar(10f / SCENES);
                break;

            case 12:
                scenes.add(new ScnKruhy(this));
                progressbar(11f / SCENES);
                break;

            case 13:
                scenes.add(new ScnMravce(this));
                progressbar(12f / SCENES);
                break;

            case 14:
                scenes.add(new ScnUmyvarka(this));
                progressbar(1f / SCENES);
                break;

//
//            case 15:
//                scenes.add(new ScnPrechod1(this));
//                progressbar(14f / SCENES);
//                break;
//
//            case 16:
//                scenes.add(new ScnPrechod1(this));
//                progressbar(15f / SCENES);
//                break;
//
//            case 17:
//                scenes.add(new ScnPrechod1(this));
//                progressbar(16f / SCENES);
//                break;
        }
    }

    private void progressbar (float progress){
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
            saveFrame("c:\\Users\\Jan\\OneDrive\\__dev\\processing_intellij\\libs\\Scenes\\frames\\"+scene.getName()+frame + ".png");
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
    }

    public void keyPressed(){
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
                scene.mode(0);
                break;
            case '1' : System.out.println("Scene mode 1");
                scene.mode(1);
                break;
            case '2' : System.out.println("Scene mode 2");
                scene.mode(2);
                break;
            case '3' : System.out.println("Scene mode 3");
                scene.mode(3);
                break;
            case '4' : System.out.println("Scene mode 4");
                scene.mode(4);
                break;
            case '5' : System.out.println("Scene mode 5");
                scene.mode(5);
                break;
            case '6' : System.out.println("Scene mode 6");
                scene.mode(6);
                break;
            case '7' : System.out.println("Scene mode 7");
                scene.mode(7);
                break;
            case '8' : System.out.println("Scene mode 8");
                scene.mode(8);
                break;
            case '9' : System.out.println("Scene mode 9");
                scene.mode(9);
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
                exit();
                break;

        }
    }

    private void printInfo() {
        System.out.println("Current scene: " + scene.getName());
//        System.out.println("Current mode: " + scene.getMode());
        System.out.println("Current frame: " + scene.getCounter());
        System.out.println("Current frame rate: " + frameRate);
    }

}
