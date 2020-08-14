package com.thiccindustries.dentoncam;
import javax.sound.sampled.*;
import javax.swing.*;

public class Application {

    public Application(){
        AudioFormat format = new AudioFormat(16000.0f, 8, 2, true, true);
        Microphone microphone = null;
        try {
             microphone = new Microphone(format);
             microphone.Open();
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "Failed to capture microphone input.", "Denton cam", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        GUIWindow window = new GUIWindow();
        window.setVisible(true);
        while(window.isVisible()){
            double level = microphone.volumeRMS(microphone.SampleAudioBytes(128), 0, 128);
            System.out.println(level);
            window.updateVolume(level);
        }

    }

    public static void main(String[] args){
        Application app = new Application();
    }
}
