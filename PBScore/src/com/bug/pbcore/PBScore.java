/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.pbcore;

import com.apple.eawt.Application;
import com.bug.vidblaster.api.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author bkantor
 */
public class PBScore extends javax.swing.JFrame {

    /**
     * Creates new form PBScore
     */
    int index = 0;
    int currentSelectionRow = 0;
    int prevSelectionRow = 0;
    public Map<String, Object> data = new HashMap<String, Object>();
    PBViewBox frame = new PBViewBox();
    PBManualTimer mTimerBox = new PBManualTimer();
    PBManualTimer mGameBox = new PBManualTimer();
    PBConfigFrame cForm = new PBConfigFrame();
    Timer gameTimer, timeTimer;
    String gameTime = "10:00";
    String timerTime = "00:00";
    String format = "HH:mm:ss";
    SimpleDateFormat ftg = new SimpleDateFormat(format);
    Boolean flTimerLogic = false, flGameLogic = false;
    long gameTimeC, timerTimeC;
    PBSoundModule sound;
    PBSaveLoadExcel sle = null;
    Vidblaster vb;

    public PBScore() {
        initComponents();
        sound = new PBSoundModule();
        ftg.setTimeZone(TimeZone.getTimeZone("UTC"));
        ImageIcon imgicon = new ImageIcon(getClass().getResource("/com/bug/resourse/images/big.png"));
        Application.getApplication().setDockIconImage(imgicon.getImage());
        setIconImage(imgicon.getImage());
        jTable1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent evt) {
                if (evt.getType() == TableModelEvent.UPDATE) {
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    Boolean flAdd = true;
                    int c = model.getRowCount();
                    c--;
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        if (evt.getLastRow() != c) {
                            flAdd = false;
                        }
                    }
                    if (flAdd) {
                        index++;
                        model.insertRow(model.getRowCount(), new Object[]{index, null, null, null, null, null, null, null, null, null});
                    }
                }

            }
        });
        gameTimer = new Timer(70, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
                formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
                long time = timer2long(gameTime) - (System.currentTimeMillis() - gameTimeC);
                if (time >= 0) {
                    jTextField10.setText(formatForDateNow.format(new Date(time)));
                    gameTimeAudioLogic();
                } else {
                    jTextField10.setText(formatForDateNow.format(new Date(0)));
                    gameTimer.stop();
                    if (jCheckSound.isSelected()) {
                        sound.setResourseName("stop.wav");
                        sound.start();
                    }
                }
                //System.out.println("====="+(new Date(time).toString())+"-----"+time+"   "+(new Date(0).toString()));
            }
        });
        timeTimer = new Timer(70, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
                formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
                long time = timer2long(timerTime) - (System.currentTimeMillis() - timerTimeC);
                if (time >= 0) {
                    jTextField11.setText(formatForDateNow.format(new Date(time)));
                    timerTimeAudioLogic();
                } else {
                    jTextField11.setText(formatForDateNow.format(new Date(0)));
                    timeTimer.stop();
                    if (jCheckSound.isSelected()) {
                        sound = new PBSoundModule();
                        sound.setResourseName("start.wav");
                        sound.start();
                    }
                    gameTimeC = System.currentTimeMillis();
                    gameTimer.start();
                }
            }
        });
        jPanel6.setComponentPopupMenu(jPopupMenu1);
        jTextField10.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                data.put("GameTime", jTextField10.getText());
                frame.viEvents.fireVBoxEventField("GameTime", data);
            }
        });
        jTextField11.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                data.put("TimerTime", jTextField11.getText());
                frame.viEvents.fireVBoxEventField("TimerTime", data);
            }
        });
        mGameBox.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent ce) {
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
            }

            @Override
            public void componentShown(ComponentEvent ce) {
            }

            @Override
            public void componentHidden(ComponentEvent ce) {
                gameTime = timer2string(mGameBox.getTimeLong());
                SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
                formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
                jTextField10.setText(formatForDateNow.format(new Date(mGameBox.getTimeLong())));
                if (gameTimer.isRunning()) {
                    startTimers();
                }
            }
        });
        mTimerBox.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent ce) {
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
            }

            @Override
            public void componentShown(ComponentEvent ce) {
            }

            @Override
            public void componentHidden(ComponentEvent ce) {
                timerTimeC = mTimerBox.getTimeLong();
                timerTime = timer2string(timerTimeC);
                SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
                formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
                jTextField11.setText(formatForDateNow.format(new Date(timerTimeC)));
                if (timeTimer.isRunning()) {
                    startTimers();
                }
            }
        });
        //test block
    }

    private void timerTimeAudioLogic() {
        if (jCheckSound.isSelected()) {
            try {
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("mm:ss");
                formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
                String time = formatForDateNow.format(new Date(ftg.parse(jTextField11.getText()).getTime()));
                if ("02:00".equals(time)) {
                    if (!flTimerLogic) {
                        sound = new PBSoundModule();
                        sound.setResourseName("2minut.wav");
                        sound.start();
                        flTimerLogic = true;
                    }
                } else if ("01:00".equals(time)) {
                    if (!flTimerLogic) {
                        sound = new PBSoundModule();
                        sound.setResourseName("1minut.wav");
                        sound.start();
                        flTimerLogic = true;
                    }
                } else if ("00:30".equals(time)) {
                    if (!flTimerLogic) {
                        sound = new PBSoundModule();
                        sound.setResourseName("30second.wav");
                        sound.start();
                        flTimerLogic = true;
                    }
                } else if ("00:10".equals(time)) {
                    if (!flTimerLogic) {
                        sound = new PBSoundModule();
                        sound.setResourseName("10second.wav");
                        sound.start();
                        flTimerLogic = true;
                    }
                } else {
                    if (flTimerLogic) {
                        flTimerLogic = false;
                    }
                }
            } catch (ParseException ex) {
                Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void gameTimeAudioLogic() {
        if (jCheckSound.isSelected()) {
            try {
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("mm:ss");
                formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
                String time = formatForDateNow.format(new Date(ftg.parse(jTextField10.getText()).getTime()));
                if ("02:00".equals(time)) {
                    if (!flGameLogic) {
                        sound = new PBSoundModule();
                        sound.setResourseName("60second.wav");
                        sound.start();
                        flGameLogic = true;
                    }
                } else {
                    if (flGameLogic) {
                        flGameLogic = false;
                    }
                }
            } catch (ParseException ex) {
                Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void addTimerTime(long time) {
        //add to timerTime time
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("mm:ss");
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        timerTime = formatForDateNow.format(new Date(timer2long(timerTime) + time));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem5 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem6 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem7 = new javax.swing.JRadioButtonMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jRadioButtonMenuItem8 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem9 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem10 = new javax.swing.JRadioButtonMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jTextField8 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jTextField9 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        tableUP = new javax.swing.JButton();
        tableRowSet = new javax.swing.JButton();
        tableDOWN = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuOpen = new javax.swing.JMenuItem();
        jMenuSave = new javax.swing.JMenuItem();
        jMenuSaveAs = new javax.swing.JMenuItem();
        jMenuClear = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jCheckViewBox = new javax.swing.JCheckBoxMenuItem();
        jCheckElTablo = new javax.swing.JCheckBoxMenuItem();
        jCheckSound = new javax.swing.JCheckBoxMenuItem();
        jCheckScore = new javax.swing.JCheckBoxMenuItem();
        jCheckStream = new javax.swing.JCheckBoxMenuItem();

        jMenu3.setText("Период");

        buttonGroup1.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setText("20 минут");
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem1);

        buttonGroup1.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setText("15 минут");
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem2);

        buttonGroup1.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setSelected(true);
        jRadioButtonMenuItem3.setText("10 минут");
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem3);

        buttonGroup1.add(jRadioButtonMenuItem4);
        jRadioButtonMenuItem4.setText("8 минут");
        jRadioButtonMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem4);

        buttonGroup1.add(jRadioButtonMenuItem5);
        jRadioButtonMenuItem5.setText("6 минут");
        jRadioButtonMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem5);

        buttonGroup1.add(jRadioButtonMenuItem6);
        jRadioButtonMenuItem6.setText("5 минут");
        jRadioButtonMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem6);

        buttonGroup1.add(jRadioButtonMenuItem7);
        jRadioButtonMenuItem7.setText("Вручную");
        jRadioButtonMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem7ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem7);

        jPopupMenu1.add(jMenu3);

        jMenu5.setText("Таймаут");

        jMenuItem1.setText("10 секунд");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem1);

        jMenuItem7.setText("30 секунд");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem7);

        jMenuItem8.setText("1 минута");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem8);

        jMenuItem9.setText("2 минуты");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem9);

        jMenuItem10.setText("5 минут");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem10);

        jMenuItem11.setText("10 минут");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem11);

        jMenuItem12.setText("вручную");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem12);

        jMenuItem13.setText("+30 секунд");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem13);

        jMenuItem14.setText("+1 минута");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem14);

        jMenuItem15.setText("+2 минуты");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem15);

        jPopupMenu1.add(jMenu5);

        jMenu6.setText("Формат");

        buttonGroup2.add(jRadioButtonMenuItem8);
        jRadioButtonMenuItem8.setText("mm:ss");
        jRadioButtonMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem8ActionPerformed(evt);
            }
        });
        jMenu6.add(jRadioButtonMenuItem8);

        buttonGroup2.add(jRadioButtonMenuItem9);
        jRadioButtonMenuItem9.setSelected(true);
        jRadioButtonMenuItem9.setText("hh:mm:ss");
        jRadioButtonMenuItem9.setToolTipText("");
        jRadioButtonMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem9ActionPerformed(evt);
            }
        });
        jMenu6.add(jRadioButtonMenuItem9);

        buttonGroup2.add(jRadioButtonMenuItem10);
        jRadioButtonMenuItem10.setText("hh:mm:ss.ms");
        jRadioButtonMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem10ActionPerformed(evt);
            }
        });
        jMenu6.add(jRadioButtonMenuItem10);

        jPopupMenu1.add(jMenu6);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PBScore");

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/Play.png"))); // NOI18N
        jButton2.setToolTipText("Старт");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.setText("Команда1");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/Apps-clock-icon.png"))); // NOI18N
        jButton1.setToolTipText("Таймаут");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField7.setFont(new java.awt.Font("Arial Black", 1, 48)); // NOI18N
        jTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField7.setText("00");

        jButton3.setFont(new java.awt.Font("Courier New", 1, 24)); // NOI18N
        jButton3.setText("-1");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Courier New", 1, 24)); // NOI18N
        jButton4.setText("+1");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField6)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField7)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jTextField7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        jTextField8.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextField8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField8.setText("Команда2");

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/Stopx.png"))); // NOI18N
        jButton5.setToolTipText("Стоп");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/Apps-clock-icon.png"))); // NOI18N
        jButton6.setToolTipText("Таймаут");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTextField9.setFont(new java.awt.Font("Arial Black", 1, 48)); // NOI18N
        jTextField9.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField9.setText("00");

        jButton7.setFont(new java.awt.Font("Courier New", 1, 24)); // NOI18N
        jButton7.setText("-1");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Courier New", 1, 24)); // NOI18N
        jButton8.setText("+1");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField8)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField9)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jTextField9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        jTextField10.setEditable(false);
        jTextField10.setBackground(new java.awt.Color(204, 255, 255));
        jTextField10.setFont(new java.awt.Font("Courier New", 1, 36)); // NOI18N
        jTextField10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField10.setText("00:00:00");
        jTextField10.setToolTipText("");
        jTextField10.setComponentPopupMenu(jPopupMenu1);

        jTextField11.setEditable(false);
        jTextField11.setBackground(new java.awt.Color(204, 255, 204));
        jTextField11.setFont(new java.awt.Font("Courier New", 1, 36)); // NOI18N
        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.setText("00:00:00");
        jTextField11.setComponentPopupMenu(jPopupMenu1);

        jButton9.setBackground(new java.awt.Color(204, 255, 255));
        jButton9.setText("Редактировать");
        jButton9.setComponentPopupMenu(jPopupMenu1);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(204, 255, 255));
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ВРЕМЯ ИГРЫ");

        jLabel2.setBackground(new java.awt.Color(204, 255, 204));
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("ТАЙМАУТ");

        jButton10.setBackground(new java.awt.Color(204, 255, 204));
        jButton10.setText("Редактировать");
        jButton10.setComponentPopupMenu(jPopupMenu1);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField11)
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButton9)
                .addGap(0, 0, 0)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton10))
        );

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {index, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Время", "Длит-ть", "Таймаут", "Команда", "Счет", "Счет", "Команда", "Таймаут", "Результат", "Результат"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setCellSelectionEnabled(true);
        jTable1.setEditingColumn(1);
        jTable1.setEditingRow(1);
        jTable1.setFocusTraversalPolicyProvider(true);
        jTable1.setGridColor(new java.awt.Color(153, 153, 153));
        jTable1.setShowGrid(true);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTable1InputMethodTextChanged(evt);
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
        }

        tableUP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/up.png"))); // NOI18N
        tableUP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableUPActionPerformed(evt);
            }
        });

        tableRowSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/settings.png"))); // NOI18N
        tableRowSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableRowSetActionPerformed(evt);
            }
        });

        tableDOWN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/Down.png"))); // NOI18N
        tableDOWN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableDOWNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableDOWN, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tableUP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tableRowSet, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tableUP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableRowSet, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableDOWN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Page 1", jPanel1);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jTextField1.setEditable(false);
        jPanel4.add(jTextField1);

        jTextField2.setEditable(false);
        jPanel4.add(jTextField2);

        jTextField3.setEditable(false);
        jPanel4.add(jTextField3);

        jTextField4.setEditable(false);
        jPanel4.add(jTextField4);

        jTextField5.setEditable(false);
        jPanel4.add(jTextField5);

        jMenu1.setText("File");

        jMenuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/Actions-document-open-icon.png"))); // NOI18N
        jMenuOpen.setText("Load Schedule");
        jMenuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuOpenActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuOpen);

        jMenuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/Usb-icon.png"))); // NOI18N
        jMenuSave.setText("Save Schedule");
        jMenuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuSave);

        jMenuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/Usb-icon.png"))); // NOI18N
        jMenuSaveAs.setText("Save Schedule As...");
        jMenuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveAsActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuSaveAs);

        jMenuClear.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/clear-icon.png"))); // NOI18N
        jMenuClear.setText("Clear Schedule");
        jMenuClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuClearActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuClear);
        jMenu1.add(jSeparator2);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/Sign-Shutdown-icon.png"))); // NOI18N
        jMenuItem6.setText("Exit");
        jMenuItem6.setToolTipText("");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/config.png"))); // NOI18N
        jMenuItem5.setText("Configure");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar1.add(jMenu2);

        jMenu4.setLabel("Modules");

        jCheckViewBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        jCheckViewBox.setText("ViewBOX");
        jCheckViewBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/viewbox.png"))); // NOI18N
        jCheckViewBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckViewBoxActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckViewBox);

        jCheckElTablo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        jCheckElTablo.setText("ELTABLO");
        jCheckElTablo.setEnabled(false);
        jCheckElTablo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/eltablo.png"))); // NOI18N
        jCheckElTablo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckElTabloActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckElTablo);

        jCheckSound.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        jCheckSound.setText("Sound");
        jCheckSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/PBLeftImage32.png"))); // NOI18N
        jCheckSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckSoundActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckSound);

        jCheckScore.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_MASK));
        jCheckScore.setText("AI ;)");
        jCheckScore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/ai.png"))); // NOI18N
        jCheckScore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckScoreActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckScore);

        jCheckStream.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_MASK));
        jCheckStream.setText("VidBlaster");
        jCheckStream.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/images/VIDBLAST.png"))); // NOI18N
        jCheckStream.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckStreamActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckStream);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckViewBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckViewBoxActionPerformed
        if (jCheckViewBox.isSelected()) {
            frame.setVisible(true);
            jTextField1.setText("ViewBOX Open");
        } else {
            frame.setVisible(false);
            jTextField1.setText("ViewBOX Close");
        }
    }//GEN-LAST:event_jCheckViewBoxActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        this.dispose();
        System.exit(0);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void tableRowSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableRowSetActionPerformed
        saveScoreRow(currentSelectionRow);
        prevSelectionRow = currentSelectionRow;
        currentSelectionRow = jTable1.getSelectedRow();
        if (currentSelectionRow != jTable1.getRowCount() - 1) {
            loadScoreRow(currentSelectionRow);
        }
    }//GEN-LAST:event_tableRowSetActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jTextField7.setText(String.format("%02d", Integer.parseInt(jTextField7.getText()) + 1));
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTable1InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTable1InputMethodTextChanged
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.addRow(new Object[]{null, null, null, null, null, null, null, null, null, null});
    }//GEN-LAST:event_jTable1InputMethodTextChanged

    private void tableUPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableUPActionPerformed
        saveScoreRow(currentSelectionRow);
        if (jTable1.getSelectedRow() > 0) {
            prevSelectionRow = currentSelectionRow;
            currentSelectionRow--;
            jTable1.setRowSelectionInterval(currentSelectionRow, currentSelectionRow);
            loadScoreRow(currentSelectionRow);
        }
    }//GEN-LAST:event_tableUPActionPerformed

    private void tableDOWNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableDOWNActionPerformed
        saveScoreRow(currentSelectionRow);
        if (jTable1.getSelectedRow() != -1 && jTable1.getSelectedRow() + 1 < jTable1.getRowCount()) {
            prevSelectionRow = currentSelectionRow;
            currentSelectionRow++;
            jTable1.setRowSelectionInterval(currentSelectionRow, currentSelectionRow);
        }
        if (currentSelectionRow != jTable1.getRowCount() - 1) {
            loadScoreRow(currentSelectionRow);
        }
    }//GEN-LAST:event_tableDOWNActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (Integer.parseInt(jTextField7.getText()) > 0) {
            jTextField7.setText(String.format("%02d", Integer.parseInt(jTextField7.getText()) - 1));
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (Integer.parseInt(jTextField9.getText()) > 0) {
            jTextField9.setText(String.format("%02d", Integer.parseInt(jTextField9.getText()) - 1));
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        jTextField9.setText(String.format("%02d", Integer.parseInt(jTextField9.getText()) + 1));
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jButton1.setEnabled(false);
        jTable1.setValueAt(true, currentSelectionRow, 3);
        addTimerTime(timer2long("01:00"));
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jButton6.setEnabled(false);
        jTable1.setValueAt(true, currentSelectionRow, 8);
        addTimerTime(timer2long("01:00"));
    }//GEN-LAST:event_jButton6ActionPerformed

    private long timer2long(String date) {
        Date parsingDate = null;
        SimpleDateFormat ft = new SimpleDateFormat("mm:ss");
        ft.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            parsingDate = ft.parse(date);
        } catch (ParseException e) {
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, e);
        }
        return parsingDate.getTime();
    }

    private String timer2string(long date) {
        Date parsingDate = new Date(date);
        SimpleDateFormat ft = new SimpleDateFormat("mm:ss");
        ft.setTimeZone(TimeZone.getTimeZone("UTC"));
        return ft.format(parsingDate);
    }

    private void startTimers() {
        try {
            if (ftg.parse(jTextField11.getText()).getTime() > 0) {
                timerTimeC = System.currentTimeMillis();
                timeTimer.start();
            } else {
                if (jCheckSound.isSelected()) {
                    sound = new PBSoundModule();
                    sound.setResourseName("start.wav");
                    sound.start();
                }
                gameTimeC = System.currentTimeMillis();
                gameTimer.start();
            }
            tableUP.setEnabled(false);
            tableRowSet.setEnabled(false);
            tableDOWN.setEnabled(false);

        } catch (ParseException e) {
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void stopTimers() {
        if (jCheckSound.isSelected()) {
            sound = new PBSoundModule();
            sound.setResourseName("stop.wav");
            sound.start();
        }
        tableUP.setEnabled(true);
        tableRowSet.setEnabled(true);
        tableDOWN.setEnabled(true);
        try {
            if (ftg.parse(jTextField11.getText()).getTime() > 0) {
                timeTimer.stop();
            } else {
                gameTimer.stop();
            }
        } catch (ParseException e) {
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        startTimers();
        try {
            System.out.println(vb.apiread("player 2", "file"));
        } catch (IOException ex) {
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        stopTimers();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        gameTime = "20:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(gameTime);
        jTextField10.setText(formatForDateNow.format(new Date(time)));
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem8ActionPerformed
        format = "mm:ss";
        ftg = new SimpleDateFormat(format);
        ftg.setTimeZone(TimeZone.getTimeZone("UTC"));
    }//GEN-LAST:event_jRadioButtonMenuItem8ActionPerformed

    private void jRadioButtonMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem9ActionPerformed
        format = "HH:mm:ss";
        ftg = new SimpleDateFormat(format);
        ftg.setTimeZone(TimeZone.getTimeZone("UTC"));
    }//GEN-LAST:event_jRadioButtonMenuItem9ActionPerformed

    private void jRadioButtonMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem10ActionPerformed
        format = "HH:mm:ss.SSS";
        ftg = new SimpleDateFormat(format);
        ftg.setTimeZone(TimeZone.getTimeZone("UTC"));
    }//GEN-LAST:event_jRadioButtonMenuItem10ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        gameTime = "15:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(gameTime);
        jTextField10.setText(formatForDateNow.format(new Date(time)));
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
        gameTime = "10:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(gameTime);
        jTextField10.setText(formatForDateNow.format(new Date(time)));
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem4ActionPerformed
        gameTime = "08:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(gameTime);
        jTextField10.setText(formatForDateNow.format(new Date(time)));
    }//GEN-LAST:event_jRadioButtonMenuItem4ActionPerformed

    private void jRadioButtonMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem5ActionPerformed
        gameTime = "06:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(gameTime);
        jTextField10.setText(formatForDateNow.format(new Date(time)));
    }//GEN-LAST:event_jRadioButtonMenuItem5ActionPerformed

    private void jRadioButtonMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem6ActionPerformed
        gameTime = "05:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(gameTime);
        jTextField10.setText(formatForDateNow.format(new Date(time)));
    }//GEN-LAST:event_jRadioButtonMenuItem6ActionPerformed

    private void jRadioButtonMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem7ActionPerformed
//        gameTime = "01:00";
//        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
//        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
//        long time = timer2long(gameTime);
//        jTextField10.setText(formatForDateNow.format(new Date(time)));
        mGameBox.setVisible(true);
    }//GEN-LAST:event_jRadioButtonMenuItem7ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        timerTime = "00:10";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(timerTime);
        jTextField11.setText(formatForDateNow.format(new Date(time)));
        startTimers();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        timerTime = "00:30";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(timerTime);
        jTextField11.setText(formatForDateNow.format(new Date(time)));
        startTimers();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        timerTime = "01:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(timerTime);
        jTextField11.setText(formatForDateNow.format(new Date(time)));
        startTimers();
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        timerTime = "02:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(timerTime);
        jTextField11.setText(formatForDateNow.format(new Date(time)));
        startTimers();
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        timerTime = "05:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(timerTime);
        jTextField11.setText(formatForDateNow.format(new Date(time)));
        startTimers();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        timerTime = "10:00";
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(format);
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = timer2long(timerTime);
        jTextField11.setText(formatForDateNow.format(new Date(time)));
        startTimers();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        mGameBox.setTimeString(jTextField10.getText().split("\\.")[0]);
        mGameBox.setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        mTimerBox.setTimeString(jTextField11.getText().split("\\.")[0]);
        mTimerBox.setVisible(true);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        mTimerBox.setVisible(true);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        addTimerTime(timer2long("00:30"));
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        addTimerTime(timer2long("01:00"));
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        addTimerTime(timer2long("02:00"));
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuOpenActionPerformed
        JFileChooser fileopen = new JFileChooser();
        fileopen.setAcceptAllFileFilterUsed(false);
        fileopen.addChoosableFileFilter(new FileNameExtensionFilter("Excell old file format", "xls"));
        fileopen.addChoosableFileFilter(new FileNameExtensionFilter("Excell file format", "xlsx"));
        int ret = fileopen.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            try {
                sle = new PBSaveLoadExcel(file);
                List<List<Object>> sleData = sle.getData();
                int i = 0;
                for (List<Object> row : sleData) {
                    int j = 1;
                    for (Object cell : row) {
                        switch (j) {
                            case 1:
                                jTable1.setValueAt(date2string(cell, "date"), i, j);
                                break;
                            case 2:
                                jTable1.setValueAt(date2string(cell, "time"), i, j);
                                break;
                            case 3:
                                jTable1.setValueAt(data2bool(cell), i, j);
                                break;
                            case 8:
                                jTable1.setValueAt(data2bool(cell), i, j);
                                break;
                            default:
                                jTable1.setValueAt(cell, i, j);
                                break;
                        }
                        j++;
                    }
                    i++;
                }
                loadScoreRow(0);
            } catch (IOException ex) {
                Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(sle.getSheetName());

        }
    }//GEN-LAST:event_jMenuOpenActionPerformed

    private void jCheckElTabloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckElTabloActionPerformed
        if (jCheckElTablo.isSelected()) {
            jTextField2.setText("ElTablo Open");
        } else {
            jTextField2.setText("ElTablo Close");
        }
    }//GEN-LAST:event_jCheckElTabloActionPerformed

    private void jCheckSoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckSoundActionPerformed
        if (jCheckSound.isSelected()) {
            jTextField3.setText("Sound Open");
        } else {
            jTextField3.setText("Sound Close");
        }
    }//GEN-LAST:event_jCheckSoundActionPerformed

    private void jCheckScoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckScoreActionPerformed
        if (jCheckScore.isSelected()) {
            jTextField4.setText("Score Open");
        } else {
            jTextField4.setText("Score Close");
        }
    }//GEN-LAST:event_jCheckScoreActionPerformed

    private void jCheckStreamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckStreamActionPerformed
        if (jCheckStream.isSelected()) {
            try {
                this.vb = new Vidblaster("79.111.15.80", 60998);
                VidblasterThreadReader ttt = vb.getIn();
                ttt.addVidblasterReadEventListener(new VidblasterReadEventListener() {
                    @Override
                    public void VidblsterReadEvent(VidblasterReadEvents e) {
                        jTextField5.setText(e.getMessage());
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                vb.close();
            } catch (IOException ex) {
                Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, ex);
            }
            jTextField5.setText("VidBlaster Close");
        }
    }//GEN-LAST:event_jCheckStreamActionPerformed

    private void jMenuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveActionPerformed
        JFileChooser filesave = new JFileChooser();
        filesave.setAcceptAllFileFilterUsed(false);
        filesave.addChoosableFileFilter(new FileNameExtensionFilter("Excell file format", "xlsx"));
        int ret = filesave.showSaveDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = filesave.getSelectedFile();
            if (!file.exists()) {

            }
        }

    }//GEN-LAST:event_jMenuSaveActionPerformed

    private void jMenuClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuClearActionPerformed

        for (int i = jTable1.getRowCount() - 2; i > -1; i--) {
            ((DefaultTableModel) jTable1.getModel()).removeRow(i);
        }
        index = 0;
        jTable1.setValueAt(0, 0, 0);
    }//GEN-LAST:event_jMenuClearActionPerformed

    private void jMenuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveAsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuSaveAsActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        cForm.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private Boolean data2bool(Object o) {
        Boolean out = true;
        String[] split = o.getClass().getCanonicalName().split("\\.");
        if (split[split.length - 1].equals("String")) {
            if (o.equals("false") || o.equals("")) {
                out = false;
            }
        }
        if (split[split.length - 1].equals("Integer")) {
            if (o.equals(0)) {
                out = false;
            }
        }
        return out;
    }

    private String date2string(Object o, String type) {
        Date d = new Date();
        SimpleDateFormat formatForDateNow;
        if (type.equals("time")) {
            formatForDateNow = new SimpleDateFormat("mm:ss");
        } else {
            formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        }
        String[] split = o.getClass().getCanonicalName().split("\\.");
        System.out.println(split[split.length - 1]);
        if (split[split.length - 1].equals("Date")) {
            return formatForDateNow.format(o);
        } else {
            return formatForDateNow.format(d);
        }
    }

    private void loadScoreRow(Integer rowIndex) {
        if (rowIndex < jTable1.getRowCount()) {
            jTextField6.setText(jTable1.getValueAt(rowIndex, 4).toString());
            if (!jTable1.getValueAt(rowIndex, 5).toString().equals("")) {
                jTextField7.setText(String.format("%02d", Integer.parseInt(jTable1.getValueAt(rowIndex, 5).toString())));
            } else {
                jTextField7.setText("");
            }
            jTextField8.setText(jTable1.getValueAt(rowIndex, 7).toString());
            if (!jTable1.getValueAt(rowIndex, 6).toString().equals("")) {
                jTextField9.setText(String.format("%02d", Integer.parseInt(jTable1.getValueAt(rowIndex, 6).toString())));
            } else {
                jTextField9.setText("");
            }
            jTextField10.setText(jTable1.getValueAt(rowIndex, 2).toString());
            //timeout button
            jButton1.setEnabled(!(Boolean) jTable1.getValueAt(rowIndex, 3));
            jButton6.setEnabled(!(Boolean) jTable1.getValueAt(rowIndex, 8));
            vBoxUpdate();
        }
    }

    private void vBoxUpdate() {
        data.put("Team1Score", Integer.parseInt(jTextField7.getText()));
        data.put("Team2Score", Integer.parseInt(jTextField9.getText()));
        data.put("Team1Name", jTextField6.getText());
        data.put("Team2Name", jTextField8.getText());
        data.put("Team1timeout", !jButton1.isEnabled());
        data.put("Team2timeout", !jButton6.isEnabled());
        data.put("GameTime", jTextField10.getText());
        data.put("TimerTime", jTextField11.getText());
        if (jCheckScore.isSelected()) {
            //System.out.println(Integer.parseInt(jTextField7.getText()));
            if (Integer.parseInt(jTextField7.getText()) > Integer.parseInt(jTextField9.getText())) {
                Integer res = Integer.parseInt(jTextField7.getText()) - Integer.parseInt(jTextField9.getText());
                data.put("Team1ScoreS", "1/" + res);
                data.put("Team2ScoreS", "0/" + (-res));
            } else if (Integer.parseInt(jTextField7.getText()) < Integer.parseInt(jTextField9.getText())) {
                Integer res = Integer.parseInt(jTextField7.getText()) - Integer.parseInt(jTextField9.getText());
                data.put("Team1ScoreS", "0/" + res);
                data.put("Team2ScoreS", "1/" + (-res));
            } else {
                data.put("Team1ScoreS", "1/0");
                data.put("Team2ScoreS", "1/0");
            }
        } else {
        }
        frame.viEvents.fireVBoxEvents(data);
    }

    private void saveScoreRow(Integer rowIndex) {
        //column 4 5 6 7 9 10
        vBoxUpdate();
        jTable1.setValueAt(jTextField6.getText(), rowIndex, 4);
        jTable1.setValueAt(jTextField7.getText(), rowIndex, 5);
        jTable1.setValueAt(jTextField8.getText(), rowIndex, 7);
        jTable1.setValueAt(jTextField9.getText(), rowIndex, 6);
        jTable1.setValueAt(jTextField10.getText(), rowIndex, 2);
        jTable1.setValueAt(!jButton1.isEnabled(), rowIndex, 3);
        jTable1.setValueAt(!jButton6.isEnabled(), rowIndex, 8);
        if (jCheckScore.isSelected()) {
            //если включен подсчет очков
            if (Integer.parseInt(jTextField7.getText()) > Integer.parseInt(jTextField9.getText())) {
                Integer res = Integer.parseInt(jTextField7.getText()) - Integer.parseInt(jTextField9.getText());
                jTable1.setValueAt("1/" + res, rowIndex, 9);
                jTable1.setValueAt("0/" + (-res), rowIndex, 10);
            } else if (Integer.parseInt(jTextField7.getText()) < Integer.parseInt(jTextField9.getText())) {
                Integer res = Integer.parseInt(jTextField7.getText()) - Integer.parseInt(jTextField9.getText());
                jTable1.setValueAt("0/" + res, rowIndex, 9);
                jTable1.setValueAt("1/" + (-res), rowIndex, 10);
            } else {
                jTable1.setValueAt("1/0", rowIndex, 9);
                jTable1.setValueAt("1/0", rowIndex, 10);

            }
        } else {
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PBScore.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PBScore.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PBScore.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PBScore.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PBScore().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBoxMenuItem jCheckElTablo;
    private javax.swing.JCheckBoxMenuItem jCheckScore;
    private javax.swing.JCheckBoxMenuItem jCheckSound;
    private javax.swing.JCheckBoxMenuItem jCheckStream;
    private javax.swing.JCheckBoxMenuItem jCheckViewBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuClear;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jMenuOpen;
    private javax.swing.JMenuItem jMenuSave;
    private javax.swing.JMenuItem jMenuSaveAs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem10;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem5;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem6;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem7;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem8;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JButton tableDOWN;
    private javax.swing.JButton tableRowSet;
    private javax.swing.JButton tableUP;
    // End of variables declaration//GEN-END:variables

}
