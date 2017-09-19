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
public class Macro {

    public void execute(Vidblaster hvb, int mNum) throws IOException{
        hvb.apiwrite("macro "+mNum, "execute", "true");
    }
    
}
