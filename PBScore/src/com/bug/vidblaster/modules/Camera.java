/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.vidblaster.modules;

import com.bug.vidblaster.api.Vidblaster;
import java.io.IOException;

/**
 *
 * @author admin
 */
public class Camera {
    public final String pin_VideoDevice="videodevice";
    
    public void setVideoDivice(Vidblaster hvb, int camNum, String videodivice) throws IOException{
        hvb.apiwrite("camera "+camNum, this.pin_VideoDevice, videodivice);
    }
    
}
