package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;
/*************************************************************************
 *  Compilation:  javac -classpath .:jl1.0.jar MP3.java         (OS X)
 *                javac -classpath .;jl1.0.jar MP3.java         (Windows)
 *  Execution:    java -classpath .:jl1.0.jar MP3 filename.mp3  (OS X / Linux)
 *                java -classpath .;jl1.0.jar MP3 filename.mp3  (Windows)
 *  
 *  Plays an MP3 file using the JLayer MP3 library.
 *
 *  Reference:  http://www.javazoom.net/javalayer/sources.html
 *
 *
 *  To execute, get the file jl1.0.jar from the website above or from
 *
 *      http://www.cs.princeton.edu/introcs/24inout/jl1.0.jar
 *
 *  and put it in your working directory with this file MP3.java.
 *
 *************************************************************************/

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import com.neuronrobotics.sdk.util.ThreadUtil;

import javazoom.jl.player.Player;
public class MP3 {


    private String filename;
    private Player player; 

    // constructor that takes the name of an MP3 file
    public MP3(String filename) {
        this.filename = filename;
    }

    public void close() { 
    	if (player != null) 
    		player.close(); 
    }

    public boolean isPlaying() {
		if(player!=null)
			return !player.isComplete();
		return false;
	}
	
	public double getPercent() {
		int numFrames = player.getNumFrames();
		if(player!=null) {
			return player.getPosition();
		}
		return 0;
	}
	private double getNumFrames() {
		// TODO Auto-generated method stub
		return player.getNumFrames();
	}

    // play the MP3 file to the sound card
    public void play() {
        try {
            FileInputStream fis     = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + filename);
            System.out.println(e);
        }

        // run in new thread to play in background
        new Thread() {
            public void run() {
                try { 
                	player.play(); 
                }catch (Exception e) {
                	System.out.println(e); 
                }
            }
        }.start();
    }


    // test client
    public static void main(String[] args) {
        String filename = "track.mp3";
        MP3 mp3 = new MP3(filename);
        mp3.play();
        System.out.println("Number of frames="+mp3.getNumFrames());
        while(mp3.isPlaying()) {
        	ThreadUtil.wait(200);
        	System.out.println("Time = "+mp3.getPercent());
        }
        mp3.close();
        System.out.println("Song done");

    }


	

}
