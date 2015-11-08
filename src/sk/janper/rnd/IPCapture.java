package sk.janper.rnd;

/**
 * Created by rndzvuk on 8.11.2015.
 */

import processing.core.PApplet;
import processing.core.PImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class IPCapture extends PImage implements Runnable {
    private PApplet parent;
    private String urlString, user, pass;
    private byte[] curFrame;
    private boolean frameAvailable;
    private Thread streamReader;
    private HttpURLConnection conn;
    private BufferedInputStream httpIn;
    private ByteArrayOutputStream jpgOut;
    private int[] newPixels;

    private boolean started = false;

    public final static String VERSION = "0.1.0";

    private int w = 0;
    private int h = 0;

    public IPCapture(PApplet parent, String urlString, String user, String pass) {
        super();
        this.parent = parent;
//        parent.registerDispose(this);
        this.urlString = urlString;
        this.user = user;
        this.pass = pass;
        this.curFrame = new byte[0];
        this.frameAvailable = false;
        this.streamReader = new Thread(this, "HTTP Stream reader");
    }

    public boolean isAvailable() {
        return frameAvailable;
    }

    public void start() {
        if (!started) {
            streamReader.start();
            started = true;
        }
    }

    public void stop() {
        try {
            jpgOut.close();
            httpIn.close();
        }
        catch (IOException e) {
            System.err.println("Error closing streams: " + e.getMessage());
        }
        conn.disconnect();
    }

    public void dispose() {
        stop();
    }

    public void run() {
        URL url;
        Base64Encoder base64 = new Base64Encoder();

        try {
            url = new URL(urlString);
//            System.out.println("URL: "+url.toString());
        }
        catch (MalformedURLException e) {
            System.err.println("Invalid URL");
            return;
        }

        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("Authorization", "Basic " + base64.encode(user + ":" + pass));
            httpIn = new BufferedInputStream(conn.getInputStream(), 8192);
//            System.out.println("Connected");
        }
        catch (IOException e) {
            System.err.println("Unable to connect: " + e.getMessage());
            return;
        }

        int prev = 0;
        int cur = 0;

        try {
            while (httpIn != null && (cur = httpIn.read()) >= 0) {
//                System.out.print("a");
                if (prev == 0xFF && cur == 0xD8) {
                    jpgOut = new ByteArrayOutputStream(8192);
                    jpgOut.write((byte)prev);
//                    System.out.print("b");
                }
                if (jpgOut != null) {
                    jpgOut.write((byte)cur);
//                    System.out.print("c");
                }
                if (prev == 0xFF && cur == 0xD9) {
//                    System.out.print("d");
                    synchronized(curFrame) {
                        curFrame = jpgOut.toByteArray();
//                        System.out.print("e");
                    }
                    frameAvailable = true;
                    jpgOut.close();
                }
                prev = cur;
            }
        }
        catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
    }

    public void read() {
        try {
            ByteArrayInputStream jpgIn = new ByteArrayInputStream(curFrame);
            BufferedImage bufImg = ImageIO.read(jpgIn);
            jpgIn.close();
            w = bufImg.getWidth();
            h = bufImg.getHeight();
            newPixels = new int[w*h];
            bufImg.getRGB(0, 0, w, h, newPixels, 0, w);
//            this.updatePixels();
//
//            if (w != this.width || h != this.height) {
////                this.resize(bufImg.getWidth(),bufImg.getHeight());
//                System.out.println("w: "+w);
//                System.out.println("h: "+h);
//                this.resize(w,h);
//            }

            frameAvailable = false;
        }
        catch (IOException e) {
            System.err.println("Error acquiring the frame: " + e.getMessage());
        }
    }

    public int[] getPixels(){
        return newPixels;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}
