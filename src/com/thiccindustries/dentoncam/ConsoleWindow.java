package com.thiccindustries.dentoncam;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Font;

import java.io.PrintStream;

public class ConsoleWindow extends JFrame{


    public ConsoleWindow(){

        setTitle("JC Denton Cam Console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        textArea.setBackground(new Color(16,16,16));
        textArea.setForeground(new Color(200,200,200));
        textArea.setFont( new Font("Monospaced", Font.PLAIN, 14) );

        TextAreaOutputStream taos = new TextAreaOutputStream(textArea);

        PrintStream ps = new PrintStream(taos);

        System.setOut( ps );
        System.setErr( ps );

        add( new JScrollPane(textArea));

        pack();
        setVisible(true);
        setSize(960, 480);
    }

}
