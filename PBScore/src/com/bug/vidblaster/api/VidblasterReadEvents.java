/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.vidblaster.api;

import java.util.EventObject;

/**
 *
 * @author admin
 */

public class VidblasterReadEvents extends EventObject {

private String message;

    public VidblasterReadEvents(Object o, String message) {
        super(o);
 	this.message = message;
   }

   public VidblasterReadEvents(Object o){
 	   this(o, "");
   }

   public VidblasterReadEvents(String s){
 	   this(null, s);
   }

   public String getMessage(){
 	  return message;
   }

   @Override
   public String toString(){
 	  return getClass().getName() + "[source = " + getSource() + ", message = " + message + "]";
   }
    
}
