package com.example.sahilsachdeva.gameserver;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class gameServer extends Activity{

    TextView tv;
    EditText edt;
    Button button;
    int players;
    Socket socket;
    loadClientThread clientThreadObject;
    boolean game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_server);
        tv = (TextView) findViewById(R.id.textView);
        edt = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        players = 1;
        game=false;

        clientThreadObject = new loadClientThread();
        clientThreadObject.execute();

        //tv.setText("Game Exited !! Start New Game !!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_server, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int KeyCode,KeyEvent event){
        if(KeyCode == KeyEvent.KEYCODE_BACK){
            System.out.println("Exiting Game !! Back Button Pressed !!");
            clientThreadObject.cancel(true);
        }
        return super.onKeyDown(KeyCode,event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class loadClientThread extends AsyncTask<String,String,String> {

        public ServerSocket serverSocket;
        BufferedReader in;
        PrintWriter pw;
        String message;

        protected String receiveMessage(){
            try {
                socket = serverSocket.accept();
                pw = new PrintWriter(socket.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                message = in.readLine();
                System.out.println("Message : "+message);
                if(message.equals("1")){
                    if(players==1)
                        pw.println("Open");
                    else
                        pw.println("Closed");
                    pw.close();
                    in.close();
                    return "Poll Request";
                }
                else if(message.equals("2")){
                    if(players!=1)
                        pw.println("Closed");
                    else{
                        game=true;
                        pw.println("Open");
                        return "Start Game";
                    }
                }
                else{
                    return message;
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return "NO MESSAGE";
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                serverSocket = new ServerSocket(1234);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            while(true){
                try{
                    if(!game)
                        publishProgress(new String[]{receiveMessage()+"",""});
                    else{
                        String msg = in.readLine();
                        if(msg!=null) {
                            if(msg.contains("TimeStamp")){
                                String[] messages = msg.split("TimeStamp");
                                publishProgress(new String[]{messages[0]+"","2",messages[1]+""});
                            }
                            else
                                publishProgress(new String[]{msg + "", ""});
                        }
                    }
                }catch(Exception ex) {
                    ex.printStackTrace();
                    break;
                }
            }
            return "";
        }

        @Override
        protected void onCancelled(){
            System.out.println("Game Over !! BYE !!");
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(final String... receivedMessage){
            if(receivedMessage[0]==null) {
                System.out.println("Cancelling");
                cancel(true);
            }
            if(!receivedMessage[0].equals("")){
                final String tvText = tv.getText().toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(receivedMessage[0].equals("Poll Request")) {
                            try {
                                tv.append("\nGot a Poll Request");
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }
                        else if(receivedMessage[0].equals("Start Game")){
                            tv.append("\nGame Started");
                            button.setOnClickListener(new Button.OnClickListener(){
                                public void onClick(View view){
                                    String message = String.valueOf(edt.getText());
                                    if(!message.equals("")){
                                        edt.setText("");
                                        Date date = new Date();
                                        pw.println(message+"TimeStamp"+date.getTime());
                                    }
                                }
                            });
                        }
                        else {
                            if(receivedMessage[0]!=null && !receivedMessage[0].isEmpty()) {
                                long time1 = Long.parseLong(receivedMessage[2]);
                                Date date = new Date();
                                long time2 = date.getTime();
                                tv.append("\n" + receivedMessage[0]+" Delay: "+(time2-time1)+" ms");
                            }
                            else {
                                System.out.println("Pressed Back Button");
                                cancel(true);
                            }
                        }
                    }
                });
            }
        }

        @Override
        protected void onPostExecute(String message){

        }

    }
}
