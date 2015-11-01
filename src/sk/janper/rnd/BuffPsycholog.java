package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PShader;

import java.util.ArrayList;

/**
 * Created by Jan on 28.10.2015.
 */
public class BuffPsycholog implements BufferShader {
    private final float BACK_START = 0f;
    private final float PHASE_0 = 1f;  //initial fade-in
    private final float PHASE_1 = 1f;  //door fade=in
    private final float PHASE_2 = 1f;  //chair fade-in
    private final float FRONT_START = BACK_START + PHASE_0 + PHASE_1 + PHASE_2;
    private final float PHASE_3 = 1f;  //front fade-in
    private final float PHASE_4 = 4f;  //just wait
    private final float BUFFER_START = FRONT_START + PHASE_3+PHASE_4;
    private final float BUFFER_FADE_IN = 1f;    //photo blending
    private final float PHASE_5 = 4f;  //just wait
    private final float PHASE_6 = 1f; //back fade-out
    private final float BACK_END = BUFFER_START + PHASE_5 + PHASE_6;
    private final float PHASE_7 = 1f;  //front 0-1 cross-fade
    private final float PHASE_8 = 2f;  //just wait
    private final float PHASE_9 = 1f;  //front 1-2 cross-fade
    private final float PHASE_10 = 2f; //just wait
    private final float PHASE_11 = 1f;  //front 2-3 cross-fade
    private final float PHASE_12 = 2f; //just wait
    private final float PHASE_13 = 1f;  //front 3-4 cross-fade
    private final float PHASE_14 = 4f; //just wait
    private final float PHASE_15 = 1f; //front fade-out
    private final float FRONT_END = BACK_END + PHASE_7+PHASE_8+PHASE_9 + PHASE_10+PHASE_11+PHASE_12+PHASE_13+PHASE_14+PHASE_15;
    private PApplet parent;
    private int counter = 0;
    private String backImageNames[] = {"psycholog_back_0.png", "psycholog_back_2.png", "psycholog_back_1.png"};
    private String frontImageNames[] = {"psycholog_front_0.png", "psycholog_front_1.png", "psycholog_front_3.png", "psycholog_front_4.png", "psycholog_front_5.png" };
    private ArrayList<PImage> backImages = new ArrayList<>();
    private ArrayList<Float> backOpacities = new ArrayList<>();
    private ArrayList<PImage> frontImages = new ArrayList<>();
    private ArrayList<Float> frontOpacities = new ArrayList<>();
    private float globalOpacity = 1f;
    private PShader shader;
    private int FPS = 60;


    public BuffPsycholog(PApplet parent) {
        this.parent = parent;
        make();
    }

    private void make() {
        //back

        shader = parent.loadShader("mixShaderFinal.glsl");

        int backCount = (backImageNames.length > 5) ? 5 : backImageNames.length;
        for (int i = 0; i < backCount; i++) {
            PImage tempImage = parent.loadImage(backImageNames[i]);
            backImages.add(tempImage);
            backOpacities.add(0f);
        }

        int frontCount = (frontImageNames.length > 5) ? 5 : frontImageNames.length;
        for (int i = 0; i < frontCount; i++) {
            PImage tempImage = parent.loadImage(frontImageNames[i]);
            frontImages.add(tempImage);
            frontOpacities.add(0f);
        }

        updateImages();
        updateOpacities();

    }

    private void updateOpacities() {
//        System.out.println("b: "+backOpacities);
//        System.out.println("f: "+frontOpacities);

        shader.set("bCount", backOpacities.size());
        shader.set("fCount", frontOpacities.size());

        for (int i = 0; i < backOpacities.size(); i++) {
            shader.set("b" + i + "Opacity", backOpacities.get(i));
        }

        for (int i = 0; i < frontOpacities.size(); i++) {
            shader.set("f" + i + "Opacity", frontOpacities.get(i));
        }

        shader.set("opacity", globalOpacity);

        shader.set("textureOpacity", getAnimOpacity());
    }

    private void updateImages() {
        shader.set("bCount", backImages.size());
        shader.set("fCount", frontImages.size());

        for (int i = 0; i < backImages.size(); i++) {
            shader.set("b" + i, backImages.get(i));
        }

        for (int i = 0; i < frontImages.size(); i++) {
            shader.set("f" + i, frontImages.get(i));
        }
    }

    @Override
    public boolean isAnim(int counter) {
        return counter >= BUFFER_START * FPS;
    }

    @Override
    public float getAnimOpacity() {
        if (isAnim(counter)) {
            return PApplet.map(counter, BUFFER_START * FPS, (BUFFER_START + BUFFER_FADE_IN) * FPS, 0f, 1f);
        } else {
            return 0f;
        }
    }

    @Override
    public void makeBack() {

        float top;
        float bottom;

        if ((counter >= BACK_START * FPS) && (counter <= BACK_END * FPS)) {

            bottom = BACK_START * FPS;
            top = (BACK_START + PHASE_0) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                backOpacities.set(0, PApplet.map(counter, bottom, top, 0f, 1f));
                backOpacities.set(1, 0f);
                backOpacities.set(2, 0f);

            }

            bottom = (BACK_START + PHASE_0) * FPS;
            top = (BACK_START + PHASE_0 + PHASE_1) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                backOpacities.set(0, 1f);
                backOpacities.set(1, PApplet.map(counter, bottom, top, 0f, 1f));
                backOpacities.set(2, 0f);

            }

            bottom = (BACK_START + PHASE_0 + PHASE_1) * FPS;
            top = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                backOpacities.set(0, 1f);
                backOpacities.set(1, 1f);
                backOpacities.set(2, PApplet.map(counter, bottom, top, 0f, 1f));

            }

            bottom = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2) * FPS;
            top = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2 + PHASE_3+PHASE_4+PHASE_5) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                backOpacities.set(0, 1f);
                backOpacities.set(1, 1f);
                backOpacities.set(2, 1f);

            }

            bottom = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2 + PHASE_3+PHASE_4+PHASE_5) * FPS;
            top = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2 + PHASE_3+PHASE_4+PHASE_5+PHASE_6) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                float opacity = PApplet.map(counter, bottom, top, 1f, 0f);
                backOpacities.set(0, opacity);
                backOpacities.set(1, opacity);
                backOpacities.set(2, opacity);
            }

        } else {
            backOpacities.set(0, 0f);
            backOpacities.set(1, 0f);
            backOpacities.set(2, 0f);
        }
    }


    @Override
    public void makeFront() {
        float top;
        float bottom;

        if ((counter >= FRONT_START * FPS) && (counter <= FRONT_END * FPS)) {
            bottom = (FRONT_START) * FPS;
            top = (FRONT_START + PHASE_3) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, PApplet.map(counter, bottom, top, 0f, 1f));
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
                frontOpacities.set(4, 0f);
            }

            bottom = (FRONT_START + PHASE_3) * FPS;
            top = (FRONT_START + PHASE_3+PHASE_4+PHASE_5+PHASE_6) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 1f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
                frontOpacities.set(4, 0f);
            }

            bottom = top;
            top = top+PHASE_7 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, PApplet.map(counter, bottom, top, 1f, 0f));
                frontOpacities.set(1, 1f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
                frontOpacities.set(4, 0f);
            }

            bottom = top;
            top = bottom+PHASE_8 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 1f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
                frontOpacities.set(4, 0f);
            }

            bottom = top;
            top = bottom+PHASE_9 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, PApplet.map(counter, bottom, top, 1f, 0f));
                frontOpacities.set(2, 1f);
                frontOpacities.set(3, 0f);
                frontOpacities.set(4, 0f);
            }

            bottom = top;
            top = bottom+PHASE_10 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 1f);
                frontOpacities.set(3, 0f);
                frontOpacities.set(4, 0f);
            }

            bottom = top;
            top = bottom+PHASE_11 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, PApplet.map(counter, bottom, top, 1f, 0f));
                frontOpacities.set(3, 1f);
                frontOpacities.set(4, 0f);
            }

            bottom = top;
            top = bottom+PHASE_12 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 1f);
                frontOpacities.set(4, 0f);
            }

            bottom = top;
            top = bottom+PHASE_13 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, PApplet.map(counter, bottom, top, 1f, 0f));
                frontOpacities.set(4, 1f);
            }

            bottom = top;
            top = bottom+PHASE_14 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
                frontOpacities.set(4, 1f);
            }

            bottom = top;
            top = FRONT_END * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
                frontOpacities.set(4, PApplet.map(counter, bottom, top, 1f, 0f));
            }


        } else {
            frontOpacities.set(0, 0f);
            frontOpacities.set(1, 0f);
            frontOpacities.set(2, 0f);
            frontOpacities.set(3, 0f);
            frontOpacities.set(4, 0f);

        }
    }

    @Override
    public void setCounter(int counter) {
        this.counter = counter;
        makeBack();
        makeFront();
        updateOpacities();
    }

    @Override
    public PShader getShader() {
        return shader;
    }

    @Override
    public PShader getShader(int counter) {
//        if ((counter>FRONT_END*FPS && counter>BACK_END*FPS)||(counter<BACK_START*FPS && counter<FRONT_START*FPS)){
//            return null;
//        } else {
            setCounter(counter);
            return getShader();
//        }
    }

    @Override
    public int getFPS() {
        return FPS;
    }

    @Override
    public void setFPS(int fps) {
        this.FPS = fps;
    }
}
