package sk.janper.rnd;

import processing.core.PApplet;

import java.awt.*;

public class Main {
    public static void main0(String args[]) {
        // full-screen mode can be activated via parameters to PApplets main method.
        PApplet.main(new String[] {"sk.janper.rnd.ProcessingSketch"});
    }

    public static void main(String args[]) {

        int primary_display = 0; //index into Graphic Devices array...

        int primary_width;

        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice devices[] = environment.getScreenDevices();
        String location;
        if(devices.length>1 ){ //we have a 2nd display/projector

            primary_width = devices[0].getDisplayMode().getWidth();
            System.out.println("Primary display width: "+primary_width);
            location = "--location="+(int)(primary_width*1.75)+",0";

        }else{//leave on primary display
            location = "--location=0,0";

        }

        String display = "--display="+primary_display+1;  //processing considers the first display to be # 1
        PApplet.main(new String[] { location , "--hide-stop", display,"sk.janper.rnd.ProcessingSketch" });

    }
}