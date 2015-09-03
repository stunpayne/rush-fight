package com.example.sahilsachdeva.gameclient;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by sahil.sachdeva on 27/08/15.
 */
public class SocketSingleton {
    public static Socket socket;
    public static PrintWriter pw;
    public static BufferedReader in;

    public static void setSocket(Socket socketPassed){
        socket=socketPassed;
    }
    public static Socket getSocket(){
        return socket;
    }
    public static void setPrintWriter(PrintWriter pwPassed){
        pw=pwPassed;
    }
    public static PrintWriter getPrintWriter(){
        return pw;
    }
    public static void setBufferedReader(BufferedReader inPassed){
        in=inPassed;
    }
    public static BufferedReader getBufferedReader(){
        return in;
    }
}
