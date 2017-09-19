/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.vidblaster.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class VidblasterThreadReader extends Thread{
    private BufferedReader in;
    private Socket socket;
    private ArrayList<VidblasterReadEventListener> listeners = new ArrayList<VidblasterReadEventListener>();
    public boolean flag = true;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(flag){
                if(in.ready()){
                    fireVidblasterReadEvent(in.readLine());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(VidblasterThreadReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        public void finish()
    {
        flag = false;
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(VidblasterThreadReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addVidblasterReadEventListener(VidblasterReadEventListener listener) {
        listeners.add(listener);
    }

    public VidblasterReadEventListener[] getVidblasterReadEventListeners() {
        return listeners.toArray(new VidblasterReadEventListener[listeners.size()]);
    }

    public void removeVidblasterReadEventListener(VidblasterReadEventListener listener) {
        listeners.remove(listener);
    }

    protected void fireVidblasterReadEvent(String message) {
        VidblasterReadEvents ev = new VidblasterReadEvents(this,message);
        for(VidblasterReadEventListener listener :listeners) {
            listener.VidblsterReadEvent(ev);
        }
    }
}
