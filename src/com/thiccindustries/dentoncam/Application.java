package com.thiccindustries.dentoncam;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import javax.swing.UIManager;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class Application {

    public double[] volumeThresholdArray;
    public Image[] imagesArray;

    public static void main(String[] args){
        //Default Values
        double minVol = 0.75;
        double maxVol = 5.00;
        String filepath = new File(".").getAbsolutePath() + "/res";

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        //Open directory selection dialog
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File("."));

        int returnValue = fc.showDialog(null, "Open Folder");

        if(returnValue == JFileChooser.APPROVE_OPTION){
            filepath = fc.getSelectedFile().getPath();
        }

        //Get min and max volume
        String sMinVolume = JOptionPane.showInputDialog("Minimum volume (default: 0.75): ");
        String sMaxVolume = JOptionPane.showInputDialog("Maximum volume (default: 5.00): ");

        if(sMinVolume != null && !sMinVolume.equals(""))
            minVol = Double.parseDouble(sMinVolume);

        if(sMaxVolume != null && !sMaxVolume.equals(""))
            maxVol = Double.parseDouble(sMaxVolume);

        //Start application with values
        new Application(minVol, maxVol, filepath);
    }

    public Application(double minVolume, double maxVolume, String filepath){

        //Create and init microphone object
        AudioFormat format = new AudioFormat(16000.0f, 8, 2, true, true);
        Microphone microphone = null;
        try {
             microphone = new Microphone(format);
             microphone.Open();
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "Failed to capture microphone input.", "Denton cam", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        //Create console window
        new ConsoleWindow();

        //Get number of valid images in selected directory
        int numStates = GetStageCountFromFilePath(filepath);
        if(numStates == -1)
            LogErrorAndExit("Empty directory.", -1);
        if(numStates == -2)
            LogErrorAndExit("No valid images in directory.", -2);

        //Load images into image array
        imagesArray = PopulateImageArray(filepath, numStates);
        if(imagesArray == null)
            LogErrorAndExit("IO Error reading file. Are they properly formatted?", -1);

        //Calculate volume threshold array
        volumeThresholdArray = PopulateThresholdArray(minVolume, maxVolume, numStates - 1);
        if(volumeThresholdArray == null)
            LogErrorAndExit("Mathematical error in volume threshold calculation.", -1);

        //Create main window and wait for initialization
        PreviewWindow view = new PreviewWindow(this);
        while(!view.READY){ /*wait*/ }
        view.setVisible(true);


        byte[] samples;
        DecimalFormat df = new DecimalFormat("###.##");

        while(view.isVisible()){
            samples = microphone.SampleAudioBytes(128);
            double level = microphone.volumeRMS(samples, 0, 128);

            view.setTitle("JC Denton Cam - Level: " + df.format(level));
            view.updateVolume(level);

        }

    }

    /*Populates an array of volume thresholds with a given minimum, maximum, and number of indices.*/
    private static double[] PopulateThresholdArray(double minVolume, double maxVolume, int numStates) {
        double[] volumeThresholdArray = new double[numStates];

        DecimalFormat formatter = new DecimalFormat("#.##");

        System.out.println("Volume range: " + minVolume + " to: " + maxVolume);
        System.out.println("Volume level array: ");
        System.out.print("{ ");

        for(int i = 0; i < numStates; i++){
            volumeThresholdArray[i] = EvaluateExponentialPoint(minVolume, maxVolume, numStates - 1, i);

            if(volumeThresholdArray[i] == -1)
                return null;

            System.out.print(formatter.format(volumeThresholdArray[i]));
            if(i < numStates - 1){
                System.out.print(" , ");
            }
        }

        System.out.print(" }");

        return volumeThresholdArray;
    }

    /*Samples an exponential function given the two points (0, minimumVolume) and (numSteps, maximumVolume) at point x = i*/
    private static double EvaluateExponentialPoint(double minimumVolume, double maximumVolume, int numSteps, int i) {
        //y = a( (a/b)^-1/c ) ^ x
        double base = Math.pow((minimumVolume / maximumVolume), ( -1f/numSteps ));

        //This shouldn't happen with valid values, but is mathematically possible with a fractional power
        if(Double.isNaN(base) || Double.isInfinite(base))
            return -1;

        return minimumVolume * Math.pow(base, i);
    }

    /*Populates an array of images from a given directory and number of images to load*/
    private static Image[] PopulateImageArray(String filePath, int numStates) {
        Image[] imageArray = new Image[numStates];

        //Populate Image Array
        for(int i = 0; i < numStates; i++){
            System.out.println("Reading image: " + filePath + "\\stage" + i + ".png");

            try {
                imageArray[i] = ImageIO.read(new File(filePath + "/stage" + i + ".png"));
            } catch (IOException e) {
                return null;
            }
        }

        return imageArray;
    }

    /*Gets the count of valid images in a directory*/
    private static int GetStageCountFromFilePath(String filePath) {
        System.out.println("Reading directory: " + new File(filePath).getAbsolutePath());

        String[] paths;
        File resDirectory = new File(filePath);

        paths = resDirectory.list();

        //No files in directory
        if(paths == null)
            return -1;

        //Set total to the number of images in the directory
        int total = paths.length;

        System.out.println("Files in directory: " + total);

        //Remove any files that aren't formatted correctly from the total
        for(String path : paths){
            if(!(path.startsWith("stage") && path.endsWith(".png")))
                total--;
        }

        System.out.println("Valid images in directory: " + total);

        //Directory has files, but none are valid
        if(total == 0)
            return -2;

        //Return the total
        return total;
    }

    /*Logs an error in the console and creates an error message with the given error_message, then exits the program with the given error_code.*/
    private static void LogErrorAndExit(String error_message, int exit_code) {
        System.out.println("ERROR: " + error_message);
        JOptionPane.showMessageDialog(null, error_message, "Denton cam", JOptionPane.ERROR_MESSAGE);
        System.exit(exit_code);
    }
}
