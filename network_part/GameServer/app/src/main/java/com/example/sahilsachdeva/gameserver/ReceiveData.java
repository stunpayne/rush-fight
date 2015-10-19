package com.example.sahilsachdeva.gameserver;

import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Created by sahil.sachdeva on 26/08/15.
 */
public class ReceiveData implements Runnable{
    PrintWriter pw;
    BufferedReader in;
    TextView tv;
    EditText edt;
    public ReceiveData(PrintWriter pw, BufferedReader in, TextView tv, EditText edt) {
        this.pw = pw;
        this.in = in;
        this.tv = tv;
        this.edt = edt;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(true){
            try {
                String receivedMessage = in.readLine();
                tv.append("Received Message : "+receivedMessage);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
