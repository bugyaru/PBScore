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
public class CharacterGenerator {

    public void setText(Vidblaster hvb, int cgNum, String text) throws IOException{
        hvb.apiwrite("cg "+cgNum, "text", text);
    }
}
