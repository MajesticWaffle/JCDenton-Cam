package com.thiccindustries.dentoncam;

import javax.swing.*;
import java.awt.*;

public class SettingsWindow extends JFrame {

    SettingsCanvas canvas;

    public SettingsWindow(){
        setLayout(new BorderLayout());
        setTitle("Denton Cam Settings");
        setResizable(false);
        add("Center", canvas);

        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);

        canvas = new SettingsCanvas();
    }
}


class SettingsCanvas extends JPanel {

    byte[] audioSampleBuffer;

    public void updatAudioSampleData(byte[] audioSamples){
        audioSampleBuffer = audioSamples;
    }

    @Override
    public void paintComponent(Graphics g){

        /*Draw visualizer Line*/

        for(int sampleIndex = 0; sampleIndex < audioSampleBuffer.length - 1; sampleIndex++){

        }
    }
}
