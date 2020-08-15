package com.thiccindustries.dentoncam;
import javax.sound.sampled.*;
import javax.swing.*;

public class Application {

    public Application(double[] values){
        AudioFormat format = new AudioFormat(16000.0f, 8, 2, true, true);
        Microphone microphone = null;
        try {
             microphone = new Microphone(format);
             microphone.Open();
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "Failed to capture microphone input.", "Denton cam", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        PreviewWindow view = new PreviewWindow(values);
        view.setVisible(true);
        byte[] samples;

        while(view.isVisible()){
            samples = microphone.SampleAudioBytes(128);
            double level = microphone.volumeRMS(samples, 0, 128);
            view.updateVolume(level);
        }

    }

    public static void main(String[] args){
        double[] values = new double[]{0.65, 2 ,4};
        if(args.length >= 3){
            values[0] = Double.valueOf(args[0]);
            values[1] = Double.valueOf(args[1]);
            values[2] = Double.valueOf(args[2]);
        }

        new Application(values);
    }
}
