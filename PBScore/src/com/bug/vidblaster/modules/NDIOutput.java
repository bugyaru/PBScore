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
public class NDIOutput {
    
    public final String pin_VideoDevice="videosource";

    public void setVideoDivice(Vidblaster hvb, int doNum, String videodivice) throws IOException{
        hvb.apiwrite("output "+doNum, this.pin_VideoDevice, videodivice);
    }
   
    public void setOn(Vidblaster hvb, int doNum) throws IOException{
        hvb.apiwrite("output "+doNum, "on", "true");
    }
   
    public void setOff(Vidblaster hvb, int doNum) throws IOException{
        hvb.apiwrite("output "+doNum, "off", "true");
    }    
    
    
}
