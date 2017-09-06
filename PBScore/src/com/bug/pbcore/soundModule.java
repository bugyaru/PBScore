/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.pbcore;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author admin
 */
public class soundModule extends Thread {

    private String resourseName = "";
    private AudioInputStream ais = null;

    @Override
    public void run() {

        try {
            ais = AudioSystem.getAudioInputStream(getClass().getResource("/com/bug/resourse/" + resourseName));
            Clip test = AudioSystem.getClip();
            test.open(ais);
            test.start();
            test.drain();
            test.close();
        } catch (Exception ex) {
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ais.close();
            } catch (IOException ex) {
                Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void finish() {
        try {
            ais.close();
        } catch (IOException ex) {
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setResourseName(String name) {
        this.resourseName = name;
    }

}
