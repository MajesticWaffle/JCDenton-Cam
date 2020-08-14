package com.thiccindustries.dentoncam;

import javax.sound.sampled.*;
import javax.swing.*;

public class Microphone {

    private TargetDataLine microphoneLine;
    private AudioFormat format;

    public Microphone(AudioFormat audioFormat) throws LineUnavailableException {
        format = audioFormat;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if(!AudioSystem.isLineSupported(info)){
            throw new LineUnavailableException();
        }

        microphoneLine = (TargetDataLine) AudioSystem.getLine(info);
    }

    /*Captures a array of bytes using the opened TargetDataLine*/
    public byte[] SampleAudioBytes(int size){
        byte[] audioBuffer = new byte[size];
        microphoneLine.read(audioBuffer, 0, audioBuffer.length);
        return audioBuffer;
    }

    /** Computes the RMS volume of a group of signal sizes */
    public double volumeRMS(byte[] audioData, int start, int length) {
        long sum = 0;
        int end = start + length;
        int len = length;
        if (end > audioData.length) {
            end = audioData.length;
            len = end - start;
        }
        if (len == 0) {
            return 0;
        }
        for (int i=start; i<end; i++) {
            sum += audioData[i];
        }
        double average = (double)sum/len;

        double sumMeanSquare = 0;;
        for (int i=start; i<end; i++) {
            double f = audioData[i] - average;
            sumMeanSquare += f * f;
        }
        double averageMeanSquare = sumMeanSquare/len;
        double rootMeanSquare = Math.sqrt(averageMeanSquare);

        return rootMeanSquare;
    }


    public void Open() throws LineUnavailableException {
        microphoneLine.open(format);
        microphoneLine.start();
    }

    public void Close(){
        microphoneLine.stop();
        microphoneLine.close();
    }
}
