package com.example.sahilsachdeva.gameclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
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
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class gameClient extends Activity {

    TextView tv;
    EditText edt;
    Button button;
    PrintWriter pw;
    BufferedReader in;
    Socket socket;
    loadClientThread clientThreadObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_game_client);
        tv = (TextView) findViewById(R.id.textView);
        edt = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        socket = SocketSingleton.getSocket();

        clientThreadObject = new loadClientThread();
        clientThreadObject.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_client, menu);
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

    private class loadClientThread extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            pw=SocketSingleton.getPrintWriter();
            in=SocketSingleton.getBufferedReader();
            publishProgress(new String[]{"","1"});
            while(true){
                try {
                    String message = in.readLine();
                    if(message==null)
                        return "";
                    if(message.contains("TimeStamp")){
                        String[] messages = message.split("TimeStamp");
                        publishProgress(new String[]{messages[0]+"","2",messages[1]+""});
                    }
                    else
                        publishProgress(new String[]{message+"","2"});
                } catch (IOException e){
                    e.printStackTrace();
                    return "";
                }
            }
        }

        @Override
        protected void onProgressUpdate(final String... receivedMessage){
            if(receivedMessage[1].equals("1")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setOnClickListener(new Button.OnClickListener() {
                            public void onClick(View view) {
                                String message = String.valueOf(edt.getText());
                                if (!message.equals("")) {
                                    edt.setText("");
                                    Date date = new Date();
                                    pw.println(message+"TimeStamp"+date.getTime());
                                }
                            }
                        });
                    }
                });
            }
            else if(receivedMessage[1].equals("2")){
                final String message = receivedMessage[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long time1 = Long.parseLong(receivedMessage[2]);
                        Date date = new Date();
                        long time2 = date.getTime();
                        tv.append("\n" + message +" Delay: "+(time2-time1)+" ms");
                    }
                });
            }
            else{
                System.out.println("");
            }
        }

        @Override
        protected void onPostExecute(String message){
            try {
                in.close();
                pw.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }

        @Override
        protected void onCancelled(){
            System.out.println("Game Over !! BYE !!");
            super.onCancelled();
        }

    }



}
