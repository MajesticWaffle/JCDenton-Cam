package com.thiccindustries.dentoncam;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.text.DecimalFormat;

public class Application {

    public Application(double[] values, String filepath){
        AudioFormat format = new AudioFormat(16000.0f, 8, 2, true, true);
        Microphone microphone = null;
        try {
             microphone = new Microphone(format);
             microphone.Open();
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "Failed to capture microphone input.", "Denton cam", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        PreviewWindow view = new PreviewWindow(values, filepath);
        view.setVisible(true);
        byte[] samples;

        while(view.isVisible()){
            samples = microphone.SampleAudioBytes(128);
            double level = microphone.volumeRMS(samples, 0, 128);
            view.updateVolume(level);
            DecimalFormat df = new DecimalFormat("###.##");
            System.out.println("Microphone level: " + df.format(level));
        }

    }

    public static void main(String[] args){
        double[] values = new double[]{0.8, 2 ,5};
        String filepath = "./res";
        if(args.length >= 1){
            filepath = args[0];
        }

        if(args.length >= 4){
            filepath = args[0];
            values[0] = Double.valueOf(args[1]);
            values[1] = Double.valueOf(args[2]);
            values[2] = Double.valueOf(args[3]);
        }
        System.out.println(new File(filepath).getAbsolutePath());
        new Application(values, filepath);
    }
}
