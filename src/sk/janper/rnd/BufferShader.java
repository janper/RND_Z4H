package sk.janper.rnd;

import processing.opengl.PShader;

/**
 * Created by Jan on 30.10.2015.
 */
public interface BufferShader {
    boolean isAnim(int counter);

    float getAnimOpacity();

    void makeBack();

    void makeFront();

    void setCounter(int counter);

    PShader getShader();

    PShader getShader(int counter);

    int getFPS();

    void setFPS(int fps);
}
