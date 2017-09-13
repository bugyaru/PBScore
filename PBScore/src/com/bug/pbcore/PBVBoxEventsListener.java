/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.pbcore;

/**
 *
 * @author admin
 */
public interface PBVBoxEventsListener {
    public void viewBoxEvent(PBViewBoxEvents e);
    public void viewBoxFieldEvent(String fieldName, PBViewBoxEvents e);
}
