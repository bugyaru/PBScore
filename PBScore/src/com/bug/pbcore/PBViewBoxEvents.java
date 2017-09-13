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


public class PBViewBoxEvents extends EventObject {

    public Map<String, Object> data = new HashMap<String, Object>();
    private String fieldName;
    private ArrayList<PBVBoxEventsListener> listeners = new ArrayList<PBVBoxEventsListener>();


    public PBViewBoxEvents(Object source, String fieldName, Map data) {
        super(source);
        this.data = data;
        this.fieldName=fieldName;
    }

    public PBViewBoxEvents(Object source) {
        this(source, null, null);
    }

    public PBViewBoxEvents(Map s) {
        this(null, null, s);
    }

    /**
     *
     * @param fieldName
     * @param s
     */
    public PBViewBoxEvents(String fieldName, Map s) {
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
        public void addVBoxEventsListener(PBVBoxEventsListener listener) {
        listeners.add(listener);
    }

    public PBVBoxEventsListener[] getVBoxEventsListeners() {
        return listeners.toArray(new PBVBoxEventsListener[listeners.size()]);
    }

    public void removeVBoxEventsListener(PBVBoxEventsListener listener) {
        listeners.remove(listener);
    }

    protected void fireVBoxEvents(Map data) {
        PBViewBoxEvents ev = new PBViewBoxEvents(this, "", data);
        for (PBVBoxEventsListener listener : listeners) {
            listener.viewBoxEvent(ev);
        }
    }

    protected void fireVBoxEventField(String fieldName, Map data) {
        PBViewBoxEvents ev = new PBViewBoxEvents(this, fieldName, data);
        for (PBVBoxEventsListener listener : listeners) {
            listener.viewBoxFieldEvent(fieldName, ev);
        }
    }
}
