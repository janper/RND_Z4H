package sk.janper.rnd;

import processing.core.PGraphics;

/**
 * Created by Jan on 28.10.2015.
 */
public interface Buffers {
    boolean isAnim(int counter);
    PGraphics getBack(int counter);
    PGraphics getFront(int counter);
    void reset();
}
