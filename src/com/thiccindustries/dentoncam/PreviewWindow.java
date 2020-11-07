package com.thiccindustries.dentoncam;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class PreviewWindow extends JFrame {

    private final MyCanvas canvas;

    public PreviewWindow(double minValue, double maxValue, String filepath){

        canvas = new MyCanvas(minValue, maxValue, filepath);
        setLayout(new BorderLayout());
        setTitle("Denton Cam Output");
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

    private double currentVolume = 0;
    private final double[] volumeThresholds;
    private final Image[] dentonStates;

    public MyCanvas(double minVolume, double maxVolume, String filePath){
        int numStates = GetStageCountFromFilePath(filePath);

        dentonStates = PopulateImageArray(filePath, numStates);
        volumeThresholds = PopulateThresholdArray(minVolume, maxVolume, numStates);

        int ix = dentonStates[0].getWidth(this);
        int iy = dentonStates[0].getHeight(this);

        System.out.println("Setting window resolution to: " + ix + "x" + iy);

        setPreferredSize(new Dimension(ix, iy));

        System.out.println("Starting.");
    }

    private double[] PopulateThresholdArray(double minVolume, double maxVolume, int numStates) {
        double[] volumeThresholdArray = new double[numStates];

        double volumeRange = maxVolume - minVolume;
        double volumeDelta = volumeRange / (double)(numStates - 1);
        DecimalFormat formatter = new DecimalFormat("#.##");


        System.out.println("Volume range: " + minVolume + " to: " + maxVolume + " with a delta of: " + formatter.format(volumeDelta));
        System.out.println("Volume level array: ");
        System.out.print("{ ");



        for(int i = 0; i < numStates; i++){
            volumeThresholdArray[i] = minVolume + (volumeDelta * i);
            System.out.print(formatter.format(volumeThresholdArray[i]));
            if(i < numStates - 1){
                System.out.print(" , ");
            }
        }

        System.out.print(" }");
        System.out.println("");

        return volumeThresholdArray;
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
            System.exit(-1);
        }

        //Return the total
        return total;
    }

    @Override
    /*I am no longer Yandere dev*/
    public void paintComponent(Graphics g){
        super.paintComponent(g);


        int currentStage = 0;
        for(int i = 0; i < dentonStates.length; i++){
            if(currentVolume >= volumeThresholds[i])
                currentStage = i;
        }

        g.drawImage(dentonStates[currentStage], 0, 0, this);
    }

    public void UpdateVolume(double newVolume){
        currentVolume = newVolume;
        repaint();

    }

}
