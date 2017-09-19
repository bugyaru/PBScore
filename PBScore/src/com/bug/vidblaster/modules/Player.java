/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.vidblaster.modules;

import com.bug.vidblaster.api.Vidblaster;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author admin
 * Player Pins
Pin
duration
Returns the duration of the file in seconds.
elapsed
Returns the elapsed time in seconds.
file
Writes or reads the name of the video file. When writing the value parameter is text string with name of the file.
pause
Pauses the player. Value parameter is ignored.
play
Starts the player. Value parameter is ignored.
playing
Reads the playing status, returns true or false.
position
Sets position in either milliseconds or percent. Value parameters is numerical string (ms) with optional percentage sign (%).
stop
Stops the player. Value parameter is ignored.
 */

public class Player {
    public void play(Vidblaster hvb, int plNum) throws IOException{
        hvb.apiwrite("player "+plNum, "play", "true");
    }
   
    public void pause(Vidblaster hvb, int plNum) throws IOException{
        hvb.apiwrite("player "+plNum, "pause", "true");
    }
   
    public void stop(Vidblaster hvb, int plNum) throws IOException{
        hvb.apiwrite("player "+plNum, "stop", "true");
    }    
    
    public void setFile(Vidblaster hvb, int plNum, File file) throws IOException{
        hvb.apiwrite("player "+plNum, "file", file.getAbsolutePath());
    }   
    
    public String getFile(Vidblaster hvb, int plNum) throws IOException, InterruptedException{
        return hvb.apiread("player "+plNum, "file");
    }      
    
}
