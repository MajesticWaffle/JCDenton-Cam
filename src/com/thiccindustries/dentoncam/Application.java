package com.thiccindustries.dentoncam;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.text.DecimalFormat;

public class Application {

    public Application(double minVolume, double maxVolume, String filepath){
        AudioFormat format = new AudioFormat(16000.0f, 8, 2, true, true);
        Microphone microphone = null;
        try {
             microphone = new Microphone(format);
             microphone.Open();
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "Failed to capture microphone input.", "Denton cam", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        PreviewWindow view = new PreviewWindow(minVolume, maxVolume, filepath);
        view.setVisible(true);
        byte[] samples;

        while(view.isVisible()){
            samples = microphone.SampleAudioBytes(128);
            double level = microphone.volumeRMS(samples, 0, 128);
            view.updateVolume(level);
            DecimalFormat df = new DecimalFormat("###.##");
        }

    }

    public static void main(String[] args){
        //Define defaults
        double minVol = 0.75;
        double maxVol = 5;
        String filepath = "E:\\User Folders\\Desktop\\newtest";

        if(args.length >= 1){
            filepath = args[0];
        }

        if(args.length >= 3){
            filepath = args[0];
            minVol = Double.parseDouble(args[1]);
            maxVol = Double.parseDouble(args[2]);
        }

        new Application(minVol, maxVol, filepath);
    }
}
