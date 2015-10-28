package sk.janper.rnd;

import processing.core.PGraphics;

/**
 * Created by Jan on 06.10.2015.
 */
public interface Scene {
    String getName();
    void start();
    void stop();
    void display(PGraphics buffer);
    void reset();
    void shuffle();
    void jitter();
    void mode(int which);
    void setBGColour(int colour);
    boolean isPlaying();
}
