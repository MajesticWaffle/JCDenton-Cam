package com.thiccindustries.dentoncam;

import javax.swing.*;
import java.awt.*;

public class PreviewWindow extends JFrame {

    private MyCanvas canvas;

    public PreviewWindow(double[] values){
        canvas = new MyCanvas(values);
        setLayout(new BorderLayout());
        setTitle("Denton Cam");
        setResizable(false);
        canvas.setPreferredSize(new Dimension(465, 465));
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

    private final Image[] dentonStates = {
            new ImageIcon( getClass().getResource("res/stage0.png") ).getImage(),
            new ImageIcon( getClass().getResource("res/stage1.png") ).getImage(),
            new ImageIcon( getClass().getResource("res/stage2.png") ).getImage(),
            new ImageIcon( getClass().getResource("res/stage3.png") ).getImage(),
    };

    public MyCanvas(double[] volumeStates){
        volumeThresholds = volumeStates;
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
