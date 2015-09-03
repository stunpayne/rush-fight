package com.example.sahilsachdeva.gameserver;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by sahil.sachdeva on 26/08/15.
 */
public class ClientThread implements Runnable{
    Socket socket;
    BufferedReader in;
    PrintWriter pw;
    int players;
    TextView tv;
    EditText edt;
    Button button;
    public ClientThread(Socket socket, int players, TextView tv, EditText edt,Button button) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.pw = new PrintWriter(socket.getOutputStream(),true);
            this.players=players;
            this.tv=tv;
            this.edt=edt;
            this.button=button;
            Thread thread = new Thread(this);
            thread.start();
        }catch(Exception ex){
            ex.printStackTrace();
            return ;
        }
    }

    @Override
    public void run() {
        int request = -1;
        try {
            request = Integer.parseInt(in.readLine());
            switch(request){
                case 1: tv.append("Got a Poll Request from Thread "+Thread.currentThread().getId()+"\n");
                        if(players==1)
                            pw.println("Open");
                        else
                            pw.println("Closed");
                        tv.append("Number of Players : "+ players+"\n");
                        return;
                case 2: tv.append("Got a Join Request from Thread "+Thread.currentThread().getId()+"\n");
                        if(players==1) {
                            pw.println("Open");
                            players++;
                            new ReceiveData(pw,in,tv,edt);
                            new SendData(pw,in,tv,edt,button);
                        }
                        else {
                            pw.println("Closed");
                            return ;
                        }
                        //break;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
