package com.example.sahilsachdeva.gameserver;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Created by sahil.sachdeva on 26/08/15.
 */
public class SendData implements Runnable{
    PrintWriter pw;
    BufferedReader in;
    TextView tv;
    EditText edt;
    Button button;
    public SendData(PrintWriter pw, BufferedReader in, TextView tv, EditText edt,Button button) {
        this.pw = pw;
        this.in = in;
        this.tv = tv;
        this.edt = edt;
        this.button = button;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        button.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                String message = String.valueOf(edt.getText());
                if(!message.equals("")){
                    edt.setText("");
                    pw.println(message);
                }
            }
        });
    }
}
