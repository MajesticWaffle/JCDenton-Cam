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
        ConsoleWindow console = new ConsoleWindow();

        //Init program variables
        int numStates = GetStageCountFromFilePath(filepath);
        imagesArray = PopulateImageArray(filepath, numStates);
        volumeThresholdArray = PopulateThresholdArray(minVolume, maxVolume, numStates - 1);

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

    private double[] PopulateThresholdArray(double minVolume, double maxVolume, int numStates) {
        double[] volumeThresholdArray = new double[numStates];

        DecimalFormat formatter = new DecimalFormat("#.##");


        System.out.println("Volume range: " + minVolume + " to: " + maxVolume);
        System.out.println("Volume level array: ");
        System.out.print("{ ");


        for(int i = 0; i < numStates; i++){
            volumeThresholdArray[i] = EvaluateExponentialPoint(minVolume, maxVolume, numStates - 1, i);
            System.out.print(formatter.format(volumeThresholdArray[i]));
            if(i < numStates - 1){
                System.out.print(" , ");
            }
        }

        System.out.print(" }");

        return volumeThresholdArray;
    }

    /*Creates and samples an exponential function given the two points (0, y1) and (steps, y2)*/
    private double EvaluateExponentialPoint(double y1, double y2, int steps, int x) {
        //y = a( (a/b)^-1/c ) ^ x

        double b = Math.pow((y1 / y2), (-1f/steps));
        return y1 * Math.pow(b, x);
    }

    private Image[] PopulateImageArray(String filePath, int numStates) {
        Image[] imageArray = new Image[numStates];

        //Populate Image Array
        for(int i = 0; i < numStates; i++){
            System.out.println("Reading image: " + filePath + "\\stage" + i + ".png");

            try {
                imageArray[i] = ImageIO.read(new File(filePath + "/stage" + i + ".png"));
            } catch (IOException e) {
                System.out.println("ERROR: IO Error reading file. Are they properly formatted?");
                JOptionPane.showMessageDialog(null, "ERROR: IO Error reading file.", "Denton cam", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
        }

        return imageArray;
    }

    private int GetStageCountFromFilePath(String filePath) {
        System.out.println("Reading directory: " + new File(filePath).getAbsolutePath());

        String[] paths;
        File resDirectory = new File(filePath);

        paths = resDirectory.list();

        //No images in array
        if(paths == null){
            System.out.println("ERROR: Empty directory");
            JOptionPane.showMessageDialog(null, "ERROR: Empty directory.", "Denton cam", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        //Set total to the number of images in the directory
        int total = paths.length;

        System.out.println("Files in directory: " + total);

        //Reject any files that aren't meant for denton cam
        for(String path : paths){
            if(!(path.startsWith("stage") && path.endsWith(".png")))
                total--;
        }

        System.out.println("Valid images in directory: " + total);

        if(total == 0){
            System.out.println("ERROR: No valid images in directory");
            JOptionPane.showMessageDialog(null, "ERROR: No valid images in directory.", "Denton cam", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        //Return the total
        return total;
    }

}
