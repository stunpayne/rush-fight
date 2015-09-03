package com.example.sahilsachdeva.gameclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;


public class MainActivity extends Activity {

    Socket clientSocket;
    BufferedReader in;
    PrintWriter pw;
    EditText edt;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = getParent();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
        Date date = new Date();
        Log.e("Current Time Value1",date+"");
        String stringDate = df.format(date)+"";//will be sent through the pipeline
        //Log.e("Current Time Value2",stringDate);
        //Log.e("Current Time Value3",date.getTime()+"");
        try {
            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
            Date date2 = df2.parse(stringDate);
            Log.e("Current Time Value6",date2+"");
            //date2.setDate();
            Log.e("Current Time Value4",df2.format(date2)+"");
            Log.e("Current Time Value5",date2.getTime()+"");

            Date date3 = new Date();
            Log.e("Current Time Value8",df2.format(date3));
            Log.e("Current Time Value7",date3.getTime()+"");


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    public void pollServer(View view){
        edt = (EditText) findViewById(R.id.editText);
        String serverIP = String.valueOf(edt.getText());
        //edt.setText("");
        PingServer pingServer = new PingServer();
        Toast.makeText(getApplicationContext(),"Polling",Toast.LENGTH_SHORT).show();
        pingServer.execute(new String[]{"1",serverIP});
    }

    public void joinServer(View view){
        edt = (EditText) findViewById(R.id.editText);
        String serverIP = String.valueOf(edt.getText());
        Toast.makeText(getApplicationContext(),"Joining",Toast.LENGTH_SHORT).show();
        PingServer pingServer = new PingServer();
        pingServer.execute(new String[]{"2", serverIP});
    }

    public void displayToast(String toastString){
        Toast.makeText(this.getApplicationContext(),toastString,Toast.LENGTH_SHORT).show();
    }

    private class PingServer extends AsyncTask <String,Void,Integer>{
        @Override
        protected Integer doInBackground(String[] params) {
            String toastString="";
            int createNewIntent=0;
            System.out.println(params[0].toString());
            switch(params[0].toString()){
                case "1": try {
                        clientSocket = new Socket(params[1].toString(),1234);
                        pw = new PrintWriter(clientSocket.getOutputStream(),true);
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        pw.println(1);

                        String receivedMessage = in.readLine();
                        if(receivedMessage.equals("Open")){
                            //Toast.makeText(getApplicationContext(),clientSocket.getInetAddress()+" is Open",Toast.LENGTH_SHORT).show();
                            toastString = clientSocket.getInetAddress()+" is Open";
                            System.out.println(toastString);
                        }
                        else {
                            //Toast.makeText(getApplicationContext(),clientSocket.getInetAddress()+" is Closed",Toast.LENGTH_SHORT).show();
                            toastString = clientSocket.getInetAddress()+" is Closed";
                            System.out.println(toastString);
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    break;
                case "2": try {
                        clientSocket = new Socket(params[1].toString(),1234);
                        pw = new PrintWriter(clientSocket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        pw.println(2);
                        String receivedMessage = in.readLine();
                        System.out.println("In join Function");
                        if(receivedMessage.equals("Open")){
                            createNewIntent=1;
                            System.out.println("Open Server");
                            //Toast.makeText(getApplicationContext(),"Connecting to "+clientSocket.getInetAddress(),Toast.LENGTH_SHORT).show();
                            SocketSingleton.setSocket(clientSocket);
                            SocketSingleton.setBufferedReader(in);
                            SocketSingleton.setPrintWriter(pw);
                        }
                        else {
                            toastString=clientSocket.getInetAddress()+" is Closed";
                            //Toast.makeText(getApplicationContext(),clientSocket.getInetAddress()+" is Closed",Toast.LENGTH_SHORT).show();
                        }
                    }catch(Exception ex){
                    ex.printStackTrace();
                    }
                    break;
            }
            return createNewIntent;
        }

        protected void onPostExecute(Integer createNewIntent) {
            System.out.println("Create New Intent Value "+ createNewIntent);
            if(createNewIntent.intValue()==1){
                System.out.println("CreateNewIntent"+createNewIntent);
                Intent gameClientIntent = new Intent(MainActivity.this,gameClient.class);
                //gameClientIntent.putExtra("Toast","Connecting to "+clientSocket.getInetAddress());
                startActivity(gameClientIntent);
            }
        }
    }
}
