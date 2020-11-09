package com.thiccindustries.dentoncam;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Graphics;

public class PreviewWindow extends JFrame {

    public final MyCanvas canvas;

    public PreviewWindow(Application application){

        canvas = new MyCanvas(application, this);
        setLayout(new BorderLayout());
        setTitle("Denton Cam Output");
        setResizable(false);
        add("Center", canvas);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);
    }

    public void updateVolume(double volume){
        canvas.UpdateVolume(volume);
    }
}

class MyCanvas extends JPanel{

    private double currentVolume = 0;
    private Application application;

    public MyCanvas(Application applicationInstance, PreviewWindow pw){

        application = applicationInstance;
        int ix = application.imagesArray[0].getWidth(this);
        int iy = application.imagesArray[0].getHeight(this);

        System.out.println("\nSetting window resolution to: " + ix + "x" + iy);

        setPreferredSize(new Dimension(ix, iy));

        System.out.println("Starting.");
    }

    @Override
    /*I am no longer Yandere dev*/
    public void paintComponent(Graphics g){
        super.paintComponent(g);


        int currentStage = 0;
        for(int i = 1; i < application.imagesArray.length; i++){
            if(currentVolume >= application.volumeThresholdArray[i - 1])
                currentStage = i;
        }

        g.drawImage(application.imagesArray[currentStage], 0, 0, this);
    }

    public void UpdateVolume(double newVolume){
        currentVolume = newVolume;
        repaint();
    }

}
