package sk.janper.rnd;

/**
 * Created by Jan on 06.10.2015.
 */
public interface Scene {
    String getName();
    void start();
    void stop();
    void display();
    void reset();
    void shuffle();
    void jitter();
    void mode(int which);
    void setBGColour(int colour);
    boolean isPlaying();
}
