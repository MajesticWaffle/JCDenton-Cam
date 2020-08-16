package com.thiccindustries.dentoncam;

import javax.swing.*;
import java.awt.*;

public class PreviewWindow extends JFrame {

    private MyCanvas canvas;

    public PreviewWindow(double[] values, String filepath){
        canvas = new MyCanvas(values, filepath);
        setLayout(new BorderLayout());
        setTitle("Denton Cam");
        setResizable(false);
        add("Center", canvas);

        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);
    }

    public void updateVolume(double volume){
        canvas.UpdateVolume(volume);
    }
}

class MyCanvas extends JPanel{
    private double[] volumeThresholds;
    private double currentVolume = 0;

    private final Image[] dentonStates;

    private int ix, iy;

    public MyCanvas(double[] volumeStates, String filePath){

        volumeThresholds = volumeStates;

        dentonStates = new Image[]{
                new ImageIcon(filePath + "/stage0.png" ).getImage(),
                new ImageIcon(filePath + "/stage1.png" ).getImage(),
                new ImageIcon(filePath + "/stage2.png" ).getImage(),
                new ImageIcon(filePath + "/stage3.png" ).getImage(),
        };

        ix = dentonStates[0].getWidth(this);
        iy = dentonStates[0].getHeight(this);

        setPreferredSize(new Dimension(ix, iy));
    }

    @Override
    /*I AM Yandere dev*/
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if(currentVolume > volumeThresholds[2])
            g.drawImage(dentonStates[3], 0, 0, this);
        else if(currentVolume > volumeThresholds[1])
            g.drawImage(dentonStates[2], 0, 0, this);
        else if(currentVolume > volumeThresholds[0])
            g.drawImage(dentonStates[1], 0, 0, this);
        else
            g.drawImage(dentonStates[0], 0, 0, this);

    }

    public void UpdateVolume(double newVolume){
        currentVolume = newVolume;
        repaint();

    }

}
