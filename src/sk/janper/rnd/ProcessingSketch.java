package sk.janper.rnd;

import processing.core.*;

import java.util.ArrayList;

public class ProcessingSketch extends PApplet{

    private boolean record = false;
    private int[] bgColors = {0,32,64,128,192,255, color(222,177,143), color(209,175,140), color(199,172,143), color (195,171,147), color(186,171,150), color(181,172,155), color(175,173,160), color(173,176, 165), color(173,180,173), color(172,187,182), color(175,195, 193), color(175,203, 204), color(180,214, 215), color(186,226,226), color(195,237, 236)};
    private int currentBgColor = 1;
    private int cycleDir = 1;
    
    private ArrayList<Scene> scenes = new ArrayList<>();
    private int whichScene = 0;
    private Scene scene;
    
    
    public void setup() {
        size(1920, 1080, P3D);
        addScenes();
        setScene(whichScene);
        printInstructions();
    }

    private void addScenes() {
        scenes.add(new ScnUmyvarka(this));
        scenes.add(new ScnPrechod1(this));
        scenes.add(new ScnPrechod2(this));
        scenes.add(new ScnKlenotnictvo(this));
        scenes.add(new ScnMuchy(this));
        scenes.add(new ScnUrad(this));
        scenes.add(new ScnBoh(this));
    }

    public void draw (){
        background(bgColors[currentBgColor]);

        scene.setBGColour(bgColors[currentBgColor]);
        scene.display();

        recordIfNeeded();
    }


    public void setScene (int which){
        if (scene!=null){
            scene.stop();
        }
        int index = Math.abs(which % scenes.size());
        scene = scenes.get(index);
        System.out.println("Setting scene " + index + ": "+scene.getName());
        scene.start();
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
        System.out.println("0-1 : Scene modes");
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
            case 'i' : System.out.println("Current frame rate: " + frameRate);
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
        }
    }

}
