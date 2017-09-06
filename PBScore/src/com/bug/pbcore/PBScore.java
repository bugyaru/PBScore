/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.pbcore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

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
    viewBox frame = new viewBox();
    manualTimer mTimerBox = new manualTimer();
    Timer gameTimer, timeTimer;
    String gameTime = "10:00";
    String timerTime = "00:00";
    String format = "HH:mm:ss.SSS";
    SimpleDateFormat ftg = new SimpleDateFormat(format);
    Boolean flTimerLogic = false, flGameLogic = false;
    long gameTimeC, timerTimeC;
    soundModule sound;

    public PBScore() {
        initComponents();
        sound = new soundModule();
        ftg.setTimeZone(TimeZone.getTimeZone("UTC"));
        ImageIcon imgicon = new ImageIcon(getClass().getResource("/com/bug/resourse/ai.png"));
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
        gameTimer = new Timer(100, new ActionListener() {
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
        timeTimer = new Timer(100, new ActionListener() {
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
                        sound=new soundModule();
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
        //test block
        System.out.println(new Date(timer2long("01:00") + timer2long("01:00")));
    }

    private void timerTimeAudioLogic() {
        if (jCheckSound.isSelected()) {
            try {
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("mm:ss");
                formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
                String time = formatForDateNow.format(new Date(ftg.parse(jTextField11.getText()).getTime()));
                if ("02:00".equals(time)) {
                    if (!flTimerLogic) {
                        sound=new soundModule();
                        sound.setResourseName("2minut.wav");
                        sound.start();
                        flTimerLogic = true;
                    }
                } else if ("01:00".equals(time)) {
                    if (!flTimerLogic) {
                        sound=new soundModule();
                        sound.setResourseName("1minut.wav");
                        sound.start();
                        flTimerLogic = true;
                    }
                } else if ("00:30".equals(time)) {
                    if (!flTimerLogic) {
                        sound=new soundModule();
                        sound.setResourseName("30second.wav");
                        sound.start();
                        flTimerLogic = true;
                    }
                } else if ("00:10".equals(time)) {
                    if (!flTimerLogic) {
                        sound=new soundModule();
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
                        sound=new soundModule();
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
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
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
        jRadioButtonMenuItem9.setText("hh:mm:ss");
        jRadioButtonMenuItem9.setToolTipText("");
        jRadioButtonMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem9ActionPerformed(evt);
            }
        });
        jMenu6.add(jRadioButtonMenuItem9);

        buttonGroup2.add(jRadioButtonMenuItem10);
        jRadioButtonMenuItem10.setSelected(true);
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

        jButton2.setBackground(new java.awt.Color(153, 255, 102));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/start.png"))); // NOI18N
        jButton2.setToolTipText("Старт");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.setText("Команда1");

        jButton1.setBackground(new java.awt.Color(255, 255, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/ico_alpha_TaskScheduling_32x32.png"))); // NOI18N
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

        jButton5.setBackground(new java.awt.Color(255, 51, 51));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/stop.png"))); // NOI18N
        jButton5.setToolTipText("Стоп");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(255, 255, 102));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/ico_alpha_TaskScheduling_32x32.png"))); // NOI18N
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

        jTextField10.setBackground(new java.awt.Color(204, 255, 255));
        jTextField10.setFont(new java.awt.Font("Courier New", 1, 36)); // NOI18N
        jTextField10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField10.setText("00:10:00.00");
        jTextField10.setToolTipText("");
        jTextField10.setComponentPopupMenu(jPopupMenu1);

        jTextField11.setBackground(new java.awt.Color(204, 255, 204));
        jTextField11.setFont(new java.awt.Font("Courier New", 1, 36)); // NOI18N
        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.setText("00:00:00.00");
        jTextField11.setComponentPopupMenu(jPopupMenu1);

        jButton9.setBackground(new java.awt.Color(204, 255, 255));
        jButton9.setText("Редактировать");
        jButton9.setComponentPopupMenu(jPopupMenu1);

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

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/upbutton.png"))); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/ok32.png"))); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/downbutton.png"))); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
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
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/web32.png"))); // NOI18N
        jMenuItem2.setText("Load Schedule");
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/file32.png"))); // NOI18N
        jMenuItem3.setText("Save Schedule");
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/file32.png"))); // NOI18N
        jMenuItem4.setText("Save Schedule As...");
        jMenu1.add(jMenuItem4);
        jMenu1.add(jSeparator2);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/exit.png"))); // NOI18N
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
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/pdm32.png"))); // NOI18N
        jMenuItem5.setText("Configure");
        jMenu2.add(jMenuItem5);

        jMenuBar1.add(jMenu2);

        jMenu4.setLabel("Modules");

        jCheckViewBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        jCheckViewBox.setText("ViewBOX");
        jCheckViewBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/viewbox.png"))); // NOI18N
        jCheckViewBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckViewBoxActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckViewBox);

        jCheckElTablo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        jCheckElTablo.setText("ELTABLO");
        jCheckElTablo.setEnabled(false);
        jCheckElTablo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/eltablo.png"))); // NOI18N
        jMenu4.add(jCheckElTablo);

        jCheckSound.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        jCheckSound.setText("Sound");
        jCheckSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/PBLeftImage32.png"))); // NOI18N
        jMenu4.add(jCheckSound);

        jCheckScore.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_MASK));
        jCheckScore.setText("AI ;)");
        jCheckScore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/ai.png"))); // NOI18N
        jMenu4.add(jCheckScore);

        jCheckStream.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_MASK));
        jCheckStream.setText("VidBlaster");
        jCheckStream.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/bug/resourse/VIDBLAST.png"))); // NOI18N
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

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        saveScoreRow(currentSelectionRow);
        prevSelectionRow = currentSelectionRow;
        currentSelectionRow = jTable1.getSelectedRow();
        loadScoreRow(currentSelectionRow);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jTextField7.setText(String.format("%02d", Integer.parseInt(jTextField7.getText()) + 1));
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTable1InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTable1InputMethodTextChanged
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.addRow(new Object[]{null, null, null, null, null, null, null, null, null, null});
    }//GEN-LAST:event_jTable1InputMethodTextChanged

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        saveScoreRow(currentSelectionRow);
        if (jTable1.getSelectedRow() > 0) {
            prevSelectionRow = currentSelectionRow;
            currentSelectionRow--;
            loadScoreRow(currentSelectionRow);
            jTable1.setRowSelectionInterval(currentSelectionRow, currentSelectionRow);
        }
        loadScoreRow(currentSelectionRow);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        saveScoreRow(currentSelectionRow);
        if (jTable1.getSelectedRow() != -1 && jTable1.getSelectedRow() + 1 < jTable1.getRowCount()) {
            prevSelectionRow = currentSelectionRow;
            currentSelectionRow++;
            jTable1.setRowSelectionInterval(currentSelectionRow, currentSelectionRow);
        }
        loadScoreRow(currentSelectionRow);
    }//GEN-LAST:event_jButton13ActionPerformed

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

    private void startTimers() {
        try {
            if (ftg.parse(jTextField11.getText()).getTime() > 0) {
                timerTimeC = System.currentTimeMillis();
                timeTimer.start();
            } else {
                if (jCheckSound.isSelected()) {
                    sound=new soundModule();
                    sound.setResourseName("start.wav");
                    sound.start();
                }
                gameTimeC = System.currentTimeMillis();
                gameTimer.start();
            }
        } catch (ParseException e) {
            Logger.getLogger(PBScore.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void stopTimers() {
        if (jCheckSound.isSelected()) {
            sound=new soundModule();
            sound.setResourseName("stop.wav");
            sound.start();
        }
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
        mTimerBox.setVisible(true);
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
        timerTime = "5:00";
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

    private void loadScoreRow(Integer rowIndex) {
        jTextField6.setText(jTable1.getValueAt(rowIndex, 4).toString());
        jTextField7.setText(String.format("%02d", Integer.parseInt(jTable1.getValueAt(rowIndex, 5).toString())));
        jTextField8.setText(jTable1.getValueAt(rowIndex, 7).toString());
        jTextField9.setText(String.format("%02d", Integer.parseInt(jTable1.getValueAt(rowIndex, 6).toString())));
        jTextField10.setText(jTable1.getValueAt(rowIndex, 2).toString());
        //timeout button
        jButton1.setEnabled(!(Boolean) jTable1.getValueAt(rowIndex, 3));
        jButton6.setEnabled(!(Boolean) jTable1.getValueAt(rowIndex, 8));
        vBoxUpdate();
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
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
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
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
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
    // End of variables declaration//GEN-END:variables

}
