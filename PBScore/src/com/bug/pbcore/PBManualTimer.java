/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.pbcore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bkantor
 */
public class PBManualTimer extends javax.swing.JFrame {

    /**
     * "Team1Score" "Team2Score" "Team1Name" "Team2Name" "Team1timeout"
     * "Team2timeout" "GameTime" "TimerTime" "Team1ScoreS" "Team2ScoreS" 
     * Creates
     * new form viewBox
     */
    
    private String timeS;
    private long timeL; 
    private SimpleDateFormat ftg = new SimpleDateFormat("HH:mm:ss");
    public PBManualTimer() {
        ftg.setTimeZone(TimeZone.getTimeZone("UTC"));
        timeS="00:00:00";
        timeL=0;
        try{
            timeL=ftg.parse(timeS).getTime();
        }catch(ParseException ex){
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setTitle("Установить таймер");
        setAlwaysOnTop(true);
        setIconImages(null);
        setLocation(new java.awt.Point(100, 100));
        setLocationByPlatform(true);
        setModalExclusionType(null);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setText("Время (часы:минуты:секунды)");

        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.setText("00");
        jTextField11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField11FocusLost(evt);
            }
        });

        jLabel2.setText(":");

        jTextField12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField12.setText("00");
        jTextField12.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField12FocusLost(evt);
            }
        });

        jLabel3.setText(":");

        jTextField13.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField13.setText("00");
        jTextField13.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField13FocusLost(evt);
            }
        });

        jButton1.setText("Установить");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField13FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField13FocusLost
        if(!jTextField13.getText().equals("00")){
            if(!jButton1.isEnabled()) jButton1.setEnabled(true);
        }
    }//GEN-LAST:event_jTextField13FocusLost

    private void jTextField12FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusLost
        if(!jTextField12.getText().equals("00")){
            if(!jButton1.isEnabled()) jButton1.setEnabled(true);
        }
    }//GEN-LAST:event_jTextField12FocusLost

    private void jTextField11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusLost
        if(!jTextField11.getText().equals("00")){
            if(!jButton1.isEnabled()) jButton1.setEnabled(true);
        }
    }//GEN-LAST:event_jTextField11FocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(Integer.parseInt(jTextField13.getText())>59){
            jTextField13.setText("59");
        }else if(Integer.parseInt(jTextField13.getText())<0){
            jTextField13.setText("00");
        }
        if(Integer.parseInt(jTextField12.getText())>59){
            jTextField12.setText("59");
        }else if(Integer.parseInt(jTextField12.getText())<0){
            jTextField12.setText("00");
        }
        if(Integer.parseInt(jTextField11.getText())>23){
            jTextField11.setText("23");
            jTextField12.setText("59");
            jTextField13.setText("59");
        }else if(Integer.parseInt(jTextField11.getText())<0){
            jTextField11.setText("00");
        }
        jTextField11.setText(String.format("%02d", Integer.parseInt(jTextField11.getText())));
        jTextField12.setText(String.format("%02d", Integer.parseInt(jTextField12.getText())));
        jTextField13.setText(String.format("%02d", Integer.parseInt(jTextField13.getText())));
        timeS=""+jTextField11.getText()+":"+jTextField12.getText()+":"+jTextField13.getText();
        try{
            timeL=ftg.parse(timeS).getTime();
        }catch(ParseException ex){
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
        }        
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        String[] r=timeS.split(":");
        if(r.length<3){
            jTextField11.setText("00");
            jTextField12.setText(r[0]);
            jTextField13.setText(r[1]);
        }else{
            jTextField11.setText(r[0]);
            jTextField12.setText(r[1]);
            jTextField13.setText(r[2]);
        }
    }//GEN-LAST:event_formComponentShown
    public String getTimeString(){
        return timeS;
    }
    public long getTimeLong(){
        return timeL;
    }
    public void setTimeString(String s){
        this.timeS=s;
        try{
            timeL=ftg.parse(s).getTime();
        }catch(ParseException ex){
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    /**
     * @param args the command line arguments
     */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    // End of variables declaration//GEN-END:variables
}
