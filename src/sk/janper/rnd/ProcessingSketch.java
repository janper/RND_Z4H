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

    private final int SCENES = 12;
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
    private boolean blur = true;
    private boolean vignete = true;
    private PGraphics buffer;
    private int mode = 0;

    private char actionChar;
    private boolean action = false;

    private OscP5 osc;
    private NetAddress broadcastLocation;
    private boolean blank = true;

    private float blankFPS  = 60;
    private final float BLANK_SECONDS = 1f;

    private int blankTime = 1000;

    private boolean allowAudio = false;

    private boolean audioShuffle = true;

    Minim minim;
    AudioInput in;
    private PShader dimShader;
    private PShader vigneteShader;

    private int stage = 0;
    private int subStage = 0;
    private boolean stageChange = true;

//    private SecondSketch secondSketch;

    public void settings(){
//        secondSketch=new SecondSketch();
//        secondSketch.settings();
//        secondSketch.setMainWindow(this);


        GraphicsDevice devices[] = getDevices();

        if(devices.length>1){
            System.out.println("More displays detected");
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
        dimShader = loadShader("dimmer.glsl");
        vigneteShader = loadShader("vignette.glsl");
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
//        System.out.println("Audio: "+val);
//        if (val>80){
//            action=true;
//            mode+=modeDirection;
//            modeDirection*=-1;
//            actionChar = 'm';
//            System.out.println("Audio: mode up");
//            return;
//        }
        if (val>60 && audioShuffle){
            action=true;
            actionChar = 'q';
//            System.out.println("Audio: shuffle");
            return;
        }
        if (val>30){
            action=true;
            actionChar = 'w';
//            System.out.println("Audio: jitter");
            return;
        }
    }


    private void connect() {
        osc = new OscP5(this,8000);
        broadcastLocation = new NetAddress("192.168.1.4", 8090);
    }

    public void draw () {

        if (frameCount < SCENES + 2) {
            background(0);
            loadScene();
        }

        if (frameCount == SCENES + 2) {
            background(0);
            progressbar(1);
            for (int i = 0; i < SCENES; i++) {
                setScene(i);
                scene.display();
            }
            setScene(whichScene);
            pushStyle();
            fill(0);
            rect (0,0,width, height);
            popStyle();
        }

        if (frameCount > SCENES + 2) {

            lineUp();

            if (allowAudio) {
                checkAudio();
            }

            if (action) {
                action(actionChar);
                action = false;
            }

            scene.setBGColour(bgColors[currentBgColor]);
            boolean inRange = (frameCount - blankTime < BLANK_SECONDS * blankFPS);

            if (blank && !inRange) {
                background(0);
            } else {
                background(bgColors[currentBgColor]);

                if (scene.isDirect()) {
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

                if (vignete) {
                    filter(vigneteShader);
                }

                if (inRange) {
                    if (blank) {
                        dimShader.set("dim", map(frameCount - blankTime, 0, BLANK_SECONDS * blankFPS, 0.01f, 0.99f));
                    } else {
                        float value = map(frameCount - blankTime, 0, BLANK_SECONDS * blankFPS, 0.99f, 0.01f);
                        dimShader.set("dim", value);
                    }
                    filter(dimShader);
                }
                recordIfNeeded();
            }

        }
    }

    private void lineUp() {
        //TODO: line-up


        switch(stage){
            case 0:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mSTART" + (char) 27 + "[0m");
                    System.out.println("Cakam");
                    makeBlank(true);
                    whichScene = 0;
                    setScene(whichScene);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(0).getName() + (char) 27 + "[0m");
                }
                break;
            case 1:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 1: Zela a Leo : "+scenes.get(0).getName() + (char) 27 + "[0m");
                    if (whichScene!=0){
                        whichScene = 0;
                        setScene(whichScene);
                    }
                    allowAudio=true;
                    if (abs(subStage%2)==0) {
                        scene.mode(1);
                    } else {
                        scene.mode(0);
                    }
                    blur= true;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Farebne->Ciernobiele->Farebne"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=CB, 1=FAREBNE, l=VYPNUT AUDIO"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK"+ (char) 27 + "[0m");
                }
                break;
            case 2:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v spalni)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(1).getName() + (char) 27 + "[0m");
                }
                break;

            case 3:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 2: Alica a Bohus : "+scenes.get(1).getName() + (char) 27 + "[0m");
                    if (whichScene!=1){
                        whichScene = 1;
                        setScene(whichScene);
                    }
                    allowAudio=false;
                    if (abs(subStage%2)==0) {
                        scene.mode(0);
                    }else {
                        scene.mode(1);
                    }
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Standard->Scenika->Standard"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=STANDARD, 1=RETROSPEKCIE, 2=POOBEDIE"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: BLANK"+ (char) 27 + "[0m");
                }
                break;

            case 4:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mBLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam");
                    allowAudio=false;
                    makeBlank(true);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(2).getName() + (char) 27 + "[0m");
                }
                break;

            case 5:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 3: Kruhy v obili : "+ scenes.get(2).getName() + (char) 27 + "[0m");
                    if (whichScene!=2){
                        whichScene = 2;
                        setScene(whichScene);
                    }
                    allowAudio=false;
                    scene.mode(0);
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: nic"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: q/w=SHUFFLE, k=BLANK, 9=KONIEC, 0=STANDARD"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK (az po fotkach)" + (char) 27 + "[0m");
                }
                break;

            case 6:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v kruhoch)");
                    allowAudio=false;
                    scene.mode(9);
//                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(3).getName() + (char) 27 + "[0m");
                }
                break;

            case 7:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 4: Stavebniny : "+ scenes.get(3).getName() + (char) 27 + "[0m");
                    if (whichScene!=3){
                        whichScene = 3;
                        setScene(whichScene);
                    }
                    allowAudio = abs(subStage % 2) == 0;
                    scene.mode(0);
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Audio v pesnicke->Bez audia->Audio"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=STANDARD, 1=REBRIKY HORE, 2=REBRIKY DOLE, l=AUDIO"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK" + (char) 27 + "[0m");
                }
                break;

            case 8:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v stavebninach)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: 'dupne' "+scenes.get(4).getName() + (char) 27 + "[0m");
                }
                break;

            case 9:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 5: Klenotnictvo : "+ scenes.get(4).getName() + (char) 27 + "[0m");
                    if (whichScene!=4){
                        whichScene = 4;
                        setScene(whichScene);
                    }
                    allowAudio=true;
                    scene.mode(0);
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: nic"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: q=SHUFFLE, w=JITTER, l=AUDIO on/off"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK" + (char) 27 + "[0m");
                }
                break;

            case 10:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v klenotnictve)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(5).getName() + (char) 27 + "[0m");
                }
                break;

            case 11:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 6: Umyvarka : "+ scenes.get(5).getName() + (char) 27 + "[0m");
                    if (whichScene!=5){
                        whichScene = 5;
                        setScene(whichScene);
                    }
                    allowAudio=false;
                    scene.mode(1);
                    blur= false;
                    makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: nic"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: q=SHUFFLE, k=BLANK, 1=realisticky mod"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: BLANK" + (char) 27 + "[0m");
                }
                break;

            case 12:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mBLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam");
                    allowAudio=false;
                    makeBlank(true);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(1).getName() + (char) 27 + "[0m");
                }
                break;

            case 13:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 7: Alica a Bohus : "+scenes.get(1).getName() + (char) 27 + "[0m");
                    if (whichScene!=1){
                        whichScene = 1;
                        setScene(whichScene);
                    }
                    allowAudio=false;
                    if (abs(subStage%2)==0) {
                        scene.mode(2);
                    } else {
                        scene.mode(1);
                    }
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Standard -> Alica hovori sen"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 2=STANDARD, 1=RETROSPEKCIE, k=BLANK"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: BLANK 'zaciatok pesnicky'"+ (char) 27 + "[0m");
                }
                break;

            case 14:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mBLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam");
                    allowAudio=false;
                    makeBlank(true);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(6).getName() + (char) 27 + "[0m");
                }
                break;

            case 15:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 8: Pyramidy : "+scenes.get(6).getName() + (char) 27 + "[0m");
                    if (whichScene!=6){
                        whichScene = 6;
                        setScene(whichScene);
                    }
                    allowAudio=true;
                    if (abs(subStage%2)==0) {
                        scene.mode(0);
                    } else {
                        scene.mode(8);
                    }
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Standard->Zdvihnute 'vesmirne parkoviska''"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=STANDARD, 8=ZDVIH"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK"+ (char) 27 + "[0m");
                }
                break;

            case 16:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v pyramidach)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(7).getName() + (char) 27 + "[0m");
                }
                break;

            case 17:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 9: Prechod : "+scenes.get(7).getName() + (char) 27 + "[0m");
                    if (whichScene!=7){
                        whichScene = 7;
                        setScene(whichScene);
                    }
                    allowAudio=true;
                    if (abs(subStage%2)==0) {
                        scene.mode(0);
                    } else {
                        scene.mode(1);
                    }
                    blur= true;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Predne->Bocne - pri naraze'"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=STANDARD, 1=PO NARAZE"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK"+ (char) 27 + "[0m");
                }
                break;

            case 18:
                if(stageChange) {
                    System.out.println((char) 27 + "[32m  falosnyBLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale na ceste)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(3).getName() + (char) 27 + "[0m");
                }
                break;

            case 19:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 10: Stavebniny s Bohusom : "+ scenes.get(3).getName() + (char) 27 + "[0m");
                    if (whichScene!=3){
                        whichScene = 3;
                        setScene(whichScene);
                    }
                    if (abs(subStage%4)==0) {
                        scene.mode(0);
                        allowAudio=true;
                    }
                    if (abs(subStage%4)==1) {
                        scene.mode(1);
                        allowAudio=false;
                    }
                    if (abs(subStage%4)==2) {
                        scene.mode(0);
                        scene.shuffle();
                        allowAudio=false;
                    }
                    if (abs(subStage%4)==3) {
                        scene.mode(1);
                        allowAudio=false;
                    }
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Pesnicka-> Rebriky hore->Stop 'Milota sa zlakne Bohusa' -> Hore"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=STANDARD, 1=REBRIKY HORE, 2=REBRIKY DOLE"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK" + (char) 27 + "[0m");
                }
                break;

            case 20:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v stavebninach)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(8).getName() + (char) 27 + "[0m");
                }
                break;

            case 21:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 11: Psycholog : "+ scenes.get(8).getName() + (char) 27 + "[0m");
                    if (whichScene!=8){
                        whichScene = 8;
                        setScene(whichScene);
                    }

                    if (abs(subStage%3)==0) {
                        allowAudio=false;
                        scene.mode(1);
                    }
                    if (abs(subStage%3)==1) {
                        allowAudio=false;
                        scene.mode(0);
                    }
                    if (abs(subStage%3)==2) {
                        allowAudio=true;
                        scene.mode(2);
                    }
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Farebne->Ciernobiele 'Zela sa rozbehne k doktorovi' -> Pesnicka"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=CB, 1=FAREBNE, 2=POMALY, 3=RYCHLO"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK" + (char) 27 + "[0m");
                }
                break;

            case 22:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK - PRESTAVKA - UPRAVIT KAMERU!" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v u psychologa)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(1).getName() + (char) 27 + "[0m");
                }
                break;

            case 23:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 12: Bumbajovci, Zela, Dodo: "+scenes.get(1).getName() + (char) 27 + "[0m");
                    if (whichScene!=1){
                        whichScene = 1;
                        setScene(whichScene);
                    }
                    allowAudio=false;
                    if (abs(subStage%2)==0) {
                        scene.mode(2);
                    } else {
                        scene.mode(3);
                    }
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Standard->Dodo a Kristof sami->Standard"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 2=STANDARD, 3=TMAAVE"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: BLANK"+ (char) 27 + "[0m");
                }
                break;

            case 24:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mBLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam");
                    allowAudio=false;
                    makeBlank(true);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(9).getName() + (char) 27 + "[0m");
                }
                break;

            case 25:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 13: Mravce : "+ scenes.get(9).getName() + (char) 27 + "[0m");
                    if (whichScene!=9){
                        whichScene = 9;
                        setScene(whichScene);
                    }
                    allowAudio=false;
                    if (abs(subStage%2)==0) {
                        scene.mode(0);
                    } else {
                        scene.mode(1);
                    }
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Pesnicka->Kornel"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=CHAOS, 1=KORNEL, 9=ODCHOD "+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK" + (char) 27 + "[0m");
                }

            case 26:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v mravcoch)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+ scenes.get(3).getName() +  (char) 27 + "[0m");
                }
                break;

            case 27:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 14: Stavebniny : "+ scenes.get(3).getName() + (char) 27 + "[0m");
                    if (whichScene!=3){
                        whichScene = 3;
                        setScene(whichScene);
                    }

                    if (abs(subStage%4)==0) {
                        scene.mode(0);
                        allowAudio=true;
                    }
                    if (abs(subStage%4)==1) {
                        scene.mode(2);
                        allowAudio=false;
                    }
                    if (abs(subStage%4)==2) {
                        scene.mode(0);
                        allowAudio=false;
                    }
                    if (abs(subStage%4)==3) {
                        scene.mode(2);
                        allowAudio=false;
                    }
                    blur= false;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Pesnicka->Rebriky dole->stop->Rebriky dole"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=STANDARD, 1=REBRIKY HORE, 2=REBRIKY DOLE"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK" + (char) 27 + "[0m");
                }
                break;

            case 28:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v stavebninach)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(5).getName() + (char) 27 + "[0m");
                }
                break;

            case 29:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 15: Umyvarka : "+ scenes.get(5).getName() + (char) 27 + "[0m");
                    if (whichScene!=5){
                        whichScene = 5;
                        setScene(whichScene);
                    }
                    allowAudio=false;
                    scene.mode(1);
                    blur= false;
                    makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: nic"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mPouzi: q=SHUFFLE, 1=REALISTICKY, 0=STYLIZOVANY"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: BLANK" + (char) 27 + "[0m");
                }

            case 30:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mBLANK" + (char) 27 + "[0m");
                    allowAudio=false;
                    makeBlank(true);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(0).getName() + (char) 27 + "[0m");
                }
                break;
            case 31:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 16: Zela a Furby : "+scenes.get(0).getName() + (char) 27 + "[0m");
                    if (whichScene!=0){
                        whichScene = 0;
                        setScene(whichScene);
                    }
                    allowAudio=true;
                    scene.mode(2);
                    blur= true;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: nic"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: 0=CB, 1=FAREBNE, l=VYPNUT AUDIO"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK"+ (char) 27 + "[0m");
                }
                break;
            case 32:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v spalni)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(10).getName() + (char) 27 + "[0m");
                }
                break;
            case 33:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 17: Muchy : "+ scenes.get(10).getName() + (char) 27 + "[0m");
                    if (whichScene!=10){
                        whichScene = 10;
                        setScene(whichScene);
                    }
                    allowAudio=false;
                    scene.mode(0);
                    blur= true;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: nic"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: k=BLANK"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: falosny BLANK" + (char) 27 + "[0m");
                }
                break;

            case 34:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mfalosny BLANK" + (char) 27 + "[0m");
                    System.out.println("Cakam (stale v muchach)");
                    allowAudio=false;
                    scene.mode(9);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mNasleduje: "+scenes.get(11).getName() + (char) 27 + "[0m");
                }
                break;

            case 35:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mObraz 18: Boh: "+ scenes.get(11).getName() + (char) 27 + "[0m");
                    if (whichScene!=11){
                        whichScene = 11;
                        setScene(whichScene);
                    }
                    allowAudio=true;
                    if (abs(subStage%2)==0) {
                        scene.mode(0);
                    } else {
                        scene.mode(1);
                        scene.shuffle();
                        scene.shuffle();
                    }
                    blur= true;
                    if (blank) makeBlank(false);
                    stageChange = false;
                    System.out.println((char) 27 + "[35mPrepinace N/M: Chvenie -> Shuffle 'Big bang' "+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[35mKeby nieco: w = JITTER, q=SHUFFLE"+ (char) 27 + "[0m");
                    System.out.println((char) 27 + "[31mNasleduje: KONIEC, BLANK" + (char) 27 + "[0m");
                }
                break;

            case 36:
                if(stageChange) {
                    System.out.println((char) 27 + "[32mBLANK" + (char) 27 + "[0m");
                    allowAudio=false;
                    makeBlank(true);
                    stageChange = false;
                    System.out.println((char) 27 + "[31mKONIEC" + (char) 27 + "[0m");
                }
                break;



        }


    }


    private void loadScene() {
        switch(frameCount) {
            case 1:
                progressbar(0);
                break;

            case 2:
                scenes.add(new ScnSpalna(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 3:
                scenes.add(new ScnKuchyna(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 4:
                scenes.add(new ScnKruhy(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 5:
                scenes.add(new ScnStavebniny(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 6:
                scenes.add(new ScnKlenotnictvo(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 7:
                scenes.add(new ScnUmyvarka(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 8:
                scenes.add(new ScnPyramidy(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 9:
                scenes.add(new ScnPrechod(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 10:
                scenes.add(new ScnPsycholog(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 11:
                scenes.add(new ScnMravce(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 12:
                scenes.add(new ScnMuchy(this));
                progressbar((float)(frameCount-1) / SCENES);
                break;

            case 13:
                scenes.add(new ScnBoh(this));
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
        System.out.println("h+mouse button : Halt");
        System.out.println("i : Display info");
        System.out.println("x : Start");
        System.out.println("z : Stop");
//        System.out.println("b : Change background");
        System.out.println("o: Previous scene");
        System.out.println("p: Next scene");
        System.out.println("s : Save/record frames");
        System.out.println("r : Reset");
        System.out.println("q : Shuffle");
        System.out.println("w : Jitter");
        System.out.println("a : Blur");
        System.out.println("0-1 : Scene modes");
        System.out.println("k : Blank");
        System.out.println("l : Allow audio listener");
        System.out.println("v : Vignette");
        System.out.println();
        System.out.println("SPACE : Next stage");
        System.out.println("c : Previous stage");
        System.out.println("m : Next sub-stage");
        System.out.println("n : Previous sub-stage");
        System.out.println();
        System.out.println();

        if (blank) {
            System.out.println("Currently blank");
        }
    }

    public void keyPressed(){
        if (key=='h' && mousePressed){
            osc.dispose();
            exit();
        }
        actionChar = key;
        action = true;
    }

    public void action(char key){
        switch (key){
            case 'q' : System.out.print("o");
                scene.shuffle();
                break;
            case 'w' : System.out.print(".");
                scene.jitter();
                break;
            case 'r' : System.out.println("Reset");
                scene.reset();
                currentBgColor = 0;
                break;
            case 's' :
                record = !record;
                System.out.println("Recording: " + record);
                break;
//            case 'b' : currentBgColor+=cycleDir;
//                if (currentBgColor==bgColors.length-1 || currentBgColor==0){
//                    cycleDir*=-1;
//                }
//                System.out.println("BgColor: "+currentBgColor);
//                break;
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

            case 'd':
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
//            case 'h' : System.out.println("Halt!");
//                osc.dispose();
//                exit();
//                break;
            case 'k' : System.out.println("Blank: "+!blank);
                makeBlank();
                break;
            case 'l' : System.out.println("Allow audio listener: "+!allowAudio);
                allowAudio = !allowAudio;
                break;
            case 'v' : System.out.println("Vignette: "+!vignete);
                vignete= !vignete;
                break;
            case ' ' :
//                System.out.println("Next stage: "+(stage+1));
                System.out.println();
                stageChange = true;
                subStage = 0;
                stage++;
                break;
            case 'f' :
//                System.out.println("Next stage: "+(stage+1));
                System.out.println();
                stageChange = true;
                subStage = 0;
                stage++;
                break;
            case 'c' :
                System.out.println();
                System.out.println("Previous stage: "+(stage-1));
                makeBlank(true);
                stageChange = true;
                subStage = 0;
                stage--;
                if (stage<0) stage = 0;
                break;
            case 'm' :
                System.out.println("->");
                stageChange = true;
                subStage++;
                break;
            case 'n' :
                System.out.println("<-");
                stageChange = true;
                subStage--;
                break;
        }
    }

    private void makeBlank() {
        blank = !blank;
        blankFPS = frameRate;
        blankTime = frameCount;
    }

    private void makeBlank(boolean value) {
        blank = value;
        blankFPS = frameRate;
        blankTime = frameCount;
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
                actionChar = 'd';
                action=true;
                break;
            case 'm':
                mode++;
                mode = (mode<0)?0:mode;
                mode = (mode>9)?9:mode;
                actionChar = 'd';
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
