/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.pbcore;

import java.util.EventObject;

/**
 *
 * @author admin
 */
class vBoxStructure {

    Integer ScoreTeam1;
    Integer ScoreTeam2;
    String NameTeam1;
    String NameTeam2;
    String timeout;
    String gameTime;
}

public class viewBoxEvents extends EventObject {
    private vBoxStructure data;
    public viewBoxEvents(Object source, vBoxStructure data) {
        super(source);
        this.data = data;
    }

    public viewBoxEvents(Object source) {
        this(source, null);
    }

    public viewBoxEvents(vBoxStructure s) {
        this(null, s);
    }

    public vBoxStructure getMessage() {
        return data;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source = " + getSource() + ", message = " + data + "]";
    }
}



