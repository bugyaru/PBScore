/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug.vidblaster.api;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author admin
 */
public class Vidblaster {

    private String ipAddres = "localhost";
    private Integer port = 9998;
    private Socket socket = null;
    private VidblasterThreadReader in;
    private BufferedWriter out;

    public void close() throws IOException {
        in.finish();
        socket.close();
    }

    public VidblasterThreadReader getIn() {
        return in;
    }

    public Vidblaster(String ipAddres, Integer port) throws IOException {
        this.ipAddres = ipAddres;
        this.port = port;
        socket = new Socket(ipAddres, port);
        in = new VidblasterThreadReader();
        in.setSocket(socket);
        in.start();
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public Vidblaster(String ipAddres) throws IOException {
        this.ipAddres = ipAddres;
        socket = new Socket(ipAddres, port);
        in = new VidblasterThreadReader();
        in.setSocket(socket);
        in.start();
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public Vidblaster() throws IOException {
        socket = new Socket(ipAddres, port);
        in = new VidblasterThreadReader();
        in.setSocket(socket);
        in.start();
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void apiabout() throws IOException, InterruptedException {
        out.write("\napiabout\n");
    }
//apilist player 1

    public void rawApiQuery(String s) throws IOException, InterruptedException {
        out.flush();
        out.write(("\n" + s + "\n").toCharArray(), 0, ("\n" + s + "\n").toCharArray().length);
    }

    public void apiwrite(String module, String pin, String value) throws IOException {
        String str = "\napiwrite " + module;
        str += ", " + pin;
        str += ", " + escapeSymbol(value);
        str += "\n";
        out.flush();
        out.write(str.toCharArray(), 0, str.toCharArray().length);
    }

    public String apiread(String module, String pin) throws IOException, InterruptedException {
        Socket socketR = new Socket(ipAddres, port);
        InputStream inR = socketR.getInputStream();
        OutputStream outR = socketR.getOutputStream();
        Boolean msgReadyFlag = true;
        String outData="";
        String outDatax;
        String str = "\napiread " + module + ", " + pin + "\n";
        byte[] s = new byte[4098];
        while (msgReadyFlag) {
            int i = inR.read(s);
            if (i > 0) {
                outDatax = new String(s).substring(0, i - 2);
                if (outDatax.equals("200 Welcome")) {
                    outR.flush();
                    outR.write(str.getBytes());
                } else if (new String(s).substring(i-3, i - 2).equals(".")) {
                    msgReadyFlag = false;
                    outData=new String(s).substring(0, i - 5);
                } 
            }
        }
        return outData;
    }

    private String escapeSymbol(String str) {
        String regexp = "([^\\w\\s]+)";
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            str = str.replaceAll(matcher.group(), "\\\\x" + char2hexstring(matcher.group().toCharArray()[0]));
        }
        return str;
    }

    private String char2hexstring(char symbol) {
        return Integer.toHexString((int) symbol);
    }

    public String getIpAddres() {
        return ipAddres;
    }

    public Integer getPort() {
        return port;
    }
}
