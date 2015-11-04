package sk.janper.rnd;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PShader;

import java.util.ArrayList;

/**
 * Created by Jan on 28.10.2015.
 */
public class BuffKruhy implements BufferShader {
    private final float BACK_START = 2f;
    private final float BACK_END = 1f;
    private final float FRONT_START = 0f;
    private final float PHASE_0 = 0.1f;
    private final float PHASE_1 = 10f;
    private final float PHASE_2 = 0.5f;
    private final float PHASE_3 = 0.1f;
    private final float PHASE_4 = 10f;
    private final float PHASE_5 = 0.5f;
    private final float PHASE_6 = 0.1f;
    private final float PHASE_7 = 10f;
    private final float PHASE_8 = 0.5f;
    private final float PHASE_9 = 0.1f;
    private final float PHASE_10 = 10f;
    private final float PHASE_11 = 0.5f;
    private final float BUFFER_START = PHASE_0+PHASE_1+PHASE_2+PHASE_3+PHASE_4+PHASE_5+PHASE_6+PHASE_7+PHASE_8+PHASE_9;
//    private final float BUFFER_START = 0f;
    private final float BUFFER_FADE_IN = 5f;    //photo blending
    private final float FRONT_END = BUFFER_START+PHASE_10+PHASE_11;
    private PApplet parent;
    private int counter = 0;
    private String frontImageNames[] = {"kruhy2.png","kruhy1.png", "kruhy3.png", "kruhy0.png" };
    private ArrayList<PImage> frontImages = new ArrayList<>();
    private ArrayList<Float> frontOpacities = new ArrayList<>();
    private float globalOpacity = 1f;
    private PShader shader;
    private int FPS = 60;

    public BuffKruhy(PApplet parent) {
        this.parent = parent;
        make();
    }

    private void make() {
        //back

        shader = parent.loadShader("mixShaderFinal.glsl");

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


        shader.set("fCount", frontOpacities.size());

        for (int i = 0; i < frontOpacities.size(); i++) {
            shader.set("f" + i + "Opacity", frontOpacities.get(i));
        }

        shader.set("opacity", globalOpacity);

        shader.set("textureOpacity", getAnimOpacity());
    }

    private void updateImages() {
        shader.set("fCount", frontImages.size());

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

    }


    @Override
    public void makeFront() {
        float top;
        float bottom;

        if ((counter >= FRONT_START * FPS) && (counter <= FRONT_END * FPS)) {
            bottom = (FRONT_START) * FPS;
            top = (FRONT_START + PHASE_0) * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, PApplet.map(counter, bottom, top, 0f, 1f));
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
            }

            bottom = top;
            top += PHASE_1 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, PApplet.map(counter, bottom, top, 1f, 0.25f));
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
            }

            bottom = top;
            top += PHASE_2 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
            }

            bottom = top;
            top += PHASE_3 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, PApplet.map(counter, bottom, top, 0f, 1f));
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
            }

            bottom = top;
            top += PHASE_4 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, PApplet.map(counter, bottom, top, 1f, 0.25f));
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
            }

            bottom = top;
            top += PHASE_5 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
            }

            bottom = top;
            top += PHASE_6 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(2, PApplet.map(counter, bottom, top, 0f, 1f));
                frontOpacities.set(1, 0f);
                frontOpacities.set(3, 0f);
            }

            bottom = top;
            top += PHASE_7 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(2, PApplet.map(counter, bottom, top, 1f, 0.25f));
                frontOpacities.set(1, 0f);
                frontOpacities.set(3, 0f);
            }

            bottom = top;
            top += PHASE_8 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
            }

            bottom = top;
            top += PHASE_9 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(3, PApplet.map(counter, bottom, top, 0f, 1f));
                frontOpacities.set(2, 0f);
                frontOpacities.set(1, 0f);
            }

            bottom = top;
            top += PHASE_10 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(3, PApplet.map(counter, bottom, top, 1f, 0f));
                frontOpacities.set(2, 0f);
                frontOpacities.set(1, 0f);
            }

            bottom = top;
            top += PHASE_11 * FPS;
            if ((counter >= bottom) && (counter <= top)) {
                frontOpacities.set(0, 0f);
                frontOpacities.set(1, 0f);
                frontOpacities.set(2, 0f);
                frontOpacities.set(3, 0f);
            }


        } else {
            frontOpacities.set(0, 0f);
            frontOpacities.set(1, 0f);
            frontOpacities.set(2, 0f);
            frontOpacities.set(3, 0f);
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
        if ((counter>FRONT_END*FPS && counter>BACK_END*FPS)||(counter<BACK_START*FPS && counter<FRONT_START*FPS)){
            return null;
        } else {
            setCounter(counter);
            return getShader();
        }
    }

    public boolean isJustAnim(int counter) {
        return (counter > FRONT_END * FPS && counter > BACK_END * FPS) || (counter < BACK_START * FPS && counter < FRONT_START * FPS);
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
