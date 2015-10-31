package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PShader;

import java.util.ArrayList;

/**
 * Created by rndzvuk on 31.10.15.
 */
public class BufferPrechod1 implements BufferShader{

    private final float BACK_START = 0f;
    private final float PHASE_0 = 1f;  //initial fade-in
    private final float PHASE_1 = 1f;  //cars fade-in
    private final float PHASE_2 = 1f;  //zebra fade-in
    private final float PHASE_3 = 2f;  //just wait
    private final float PHASE_4 = 1f;  //remove cars
    private final float BUFFER_START = BACK_START + PHASE_0+ PHASE_1+ PHASE_2+ PHASE_3+ PHASE_4;
    private final float BUFFER_FADE_IN = 1f;    //photo blending
    private final float PHASE_5 = 10f; //just wait
    private final float PHASE_6 = 1f;  //back fade-out
    private final float BACK_END = BUFFER_START + PHASE_5 + PHASE_6;
    private PApplet parent;
    private int counter = 0;
    private String backImageNames[] = {"prechod_back_0.png", "prechod_back_2.png", "prechod_back_1.png"};
    private ArrayList<PImage> backImages = new ArrayList<>();
    private ArrayList<Float> backOpacities = new ArrayList<>();
    private float globalOpacity = 1f;
    private PShader shader;
    private int FPS = 60;

    public BufferPrechod1(PApplet parent) {
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

        updateImages();
        updateOpacities();

    }
    private void updateOpacities() {

        shader.set("bCount", backOpacities.size());

        for (int i = 0; i < backOpacities.size(); i++) {
            shader.set("b" + i + "Opacity", backOpacities.get(i));
        }

        shader.set("opacity", globalOpacity);

        shader.set("textureOpacity", getAnimOpacity());
    }
    private void updateImages() {
        shader.set("bCount", backImages.size());

        for (int i = 0; i < backImages.size(); i++) {
            shader.set("b" + i, backImages.get(i));
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
            top = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2 + PHASE_3) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                backOpacities.set(0, 1f);
                backOpacities.set(1, 1f);
                backOpacities.set(2, 1f);
            }

            bottom = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2 + PHASE_3) * FPS;
            top = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2 + PHASE_3 + PHASE_4) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                backOpacities.set(0, 1f);
                backOpacities.set(1, PApplet.map(counter, bottom, top, 1f, 0f));
                backOpacities.set(2, 1f);
            }

            bottom = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2+ PHASE_3 + PHASE_4) * FPS;
            top = (BACK_START + PHASE_0 + PHASE_1 + PHASE_2 + PHASE_3+ PHASE_4+ PHASE_5) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                backOpacities.set(0, 1f);
                backOpacities.set(1, 0f);
                backOpacities.set(2, 1f);
            }

            bottom = (BACK_END- PHASE_6 ) * FPS;
            top = BACK_END * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                float opacity = PApplet.map(counter, bottom, top, 1f, 0f);
                backOpacities.set(0, opacity);
                backOpacities.set(1, 0f);
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

    }
    @Override
    public void setCounter(int counter) {
        this.counter = counter;
        makeBack();
//        makeFront();
        updateOpacities();
    }

    @Override
    public PShader getShader() {
        return shader;
    }

    @Override
    public PShader getShader(int counter) {
        setCounter(counter);
        if ((counter>BACK_END*FPS)||(counter<BACK_START *FPS)){
            return null;
        } else {
            return getShader();
        }
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
