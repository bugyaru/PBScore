/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.pbcore;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author admin
 */


public class viewBoxEvents extends EventObject {

    public Map<String, Object> data = new HashMap<String, Object>();
    private String fieldName;
    private ArrayList<vBoxEventsListener> listeners = new ArrayList<vBoxEventsListener>();


    public viewBoxEvents(Object source, String fieldName, Map data) {
        super(source);
        this.data = data;
        this.fieldName=fieldName;
    }

    public viewBoxEvents(Object source) {
        this(source, null, null);
    }

    public viewBoxEvents(Map s) {
        this(null, null, s);
    }

    /**
     *
     * @param fieldName
     * @param s
     */
    public viewBoxEvents(String fieldName, Map s) {
        this(null, fieldName,s);
    }

    public Map getMessage() {
        return data;
    }
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source = " + getSource() + ", message = " + data.toString() + "]";
    }
        public void addVBoxEventsListener(vBoxEventsListener listener) {
        listeners.add(listener);
    }

    public vBoxEventsListener[] getVBoxEventsListeners() {
        return listeners.toArray(new vBoxEventsListener[listeners.size()]);
    }

    public void removeVBoxEventsListener(vBoxEventsListener listener) {
        listeners.remove(listener);
    }

    protected void fireVBoxEvents(Map data) {
        viewBoxEvents ev = new viewBoxEvents(this, "", data);
        for (vBoxEventsListener listener : listeners) {
            listener.viewBoxEvent(ev);
        }
    }

    protected void fireVBoxEventField(String fieldName, Map data) {
        viewBoxEvents ev = new viewBoxEvents(this, fieldName, data);
        for (vBoxEventsListener listener : listeners) {
            listener.viewBoxFieldEvent(fieldName, ev);
        }
    }
}
