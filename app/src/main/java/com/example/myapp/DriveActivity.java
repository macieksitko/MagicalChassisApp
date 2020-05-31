package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

public class DriveActivity extends AppCompatActivity{
    BluetoothSocket btSocket;
    private boolean isBtConnected = false;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

   // SeekBar brightness;
    private ProgressDialog progress;
    private  BluetoothAdapter bluetoothAdapter,bluetoothAdapter1;
    public String address;
    Button forwardBtn,backwardBtn,turnLeftBtn,turnRightBtn,sendBtn,lightsBtn,buzzerBtn,getMeasurementBtn,stopConnection;
    TinyDB keepMyAddress;
    String myAddress;
    TextView latestMeasurement;
    TinyDB tinydb,connectedAddress1,isConnected;
    String measurement;
    //ConnectBT connectBT;
    int control = 1;
    boolean lightsOnControl=false;
    boolean buzzerOnControl=false;
    boolean isDeviceConnected = false;
    ImageView okConnected,notConnected;
    ArrayList<Measurement> listOfMeasurements = new ArrayList<Measurement>();

    int i;
    private InputStream inStream;
    LinkedList<Byte> dataQueue = new LinkedList<Byte>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        forwardBtn = (Button) findViewById(R.id.forwardBtn);
        backwardBtn = (Button) findViewById(R.id.backwardBtn);
        turnLeftBtn = (Button) findViewById(R.id.turnLeftBtn);
        turnRightBtn = (Button) findViewById(R.id.turnRightBtn);
        sendBtn = (Button) findViewById(R.id.send);

        buzzerBtn = (Button) findViewById(R.id.buzzerBtn);
        lightsBtn = (Button) findViewById(R.id.lightsBtn);
        getMeasurementBtn = (Button) findViewById(R.id.getMeasurementBtn);
        latestMeasurement = (TextView) findViewById(R.id.latestMeasurement);
        stopConnection = (Button) findViewById(R.id.unknownBtn2);




        isConnected = new TinyDB(this);
        keepMyAddress = new TinyDB(this);
        connectedAddress1 = new TinyDB(this);
        myAddress = keepMyAddress.getString("address");



        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isDeviceConnected = isConnected.getBoolean("isConnected");
                okConnected = (ImageView) findViewById(R.id.okConnected);
                notConnected = (ImageView) findViewById(R.id.notConnected);
                if(isDeviceConnected){
                    notConnected.setAlpha(0);
                    okConnected.setAlpha(1.0f);
                }else{
                    okConnected.setAlpha(0);
                    notConnected.setAlpha(1.0f);
                }
            }
        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(control==1) {
            MyApplication.getApplication().setupBluetoothConnection();
            control = 0;
        }
        i=1;
    }

    @Override
    protected void onResume() {
        super.onResume();


        //Intent newint = getIntent();
        //address = newint.getStringExtra(BtActivity.EXTRA_ADDRESS);
        if(bluetoothAdapter==null){
            showToast("Your device doesnt support Bluetooth");
        }
        else if(myAddress==null){
            showToast("Any address has not been received");
        }
        else {
            if (!bluetoothAdapter.isEnabled()) {
                showToast("Bluetooth is not enabled, going to Connection...");
                openBtActivity();
            } else {

                showToast("Now you can drive!");
                //executing AsyncTask

                btSocket = MyApplication.getApplication().getCurrentBluetoothConnection();

                //buttons responsible for steering
                forwardBtn.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showToast("Going forward...");
                        driveForward();
                    }
                }));
                backwardBtn.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showToast("Going forward...");
                        driveBackward();
                    }
                }));
                turnLeftBtn.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showToast("Going forward...");
                        driveToLeft();
                    }
                }));
                turnRightBtn.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showToast("Going forward...");
                        driveToRight();
                    }
                }));

                //buttons responsible for other utilities - lights and buzzer so far
                lightsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(lightsOnControl) {
                           lightsOff();
                           lightsOnControl=false;
                       }
                       else{
                           lightsOn();
                           lightsOnControl=true;
                       }

                    }
                });
               buzzerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(buzzerOnControl) {
                            buzzerOff();
                            buzzerOnControl=false;
                        }
                        else{
                            buzzerOn();
                            buzzerOnControl=true;
                        }
                    }
                });
                stopConnection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopMeasurementRequest();

                        String measurement = receiveData();
                        latestMeasurement.setText(measurement+" m");
                    }
                });
               getMeasurementBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       getMeasurementRequest();
                   }
               });
}
        }
        //Test passing data between this activity and table in CalcActivity
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast("Sending measurement...");

                    measurement = latestMeasurement.getText().toString();
                    sendMeasurement(measurement);
                }
            });
    }



/*
    @Override
    protected void onDestroy(){
        super.onDestroy();
        MyApplication.getApplication().closeBluetoothConnection();
    }
*/

   /* private class ConnectBT extends AsyncTask<Void,Void,Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            isConnected = new TinyDB(DriveActivity.this);
            //isConnected.putBoolean("isConnected",false);
            //progress = ProgressDialog.show(DriveActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
            showToast("Connecting, please wait");
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    bluetoothAdapter1 = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dev = bluetoothAdapter1.getRemoteDevice(myAddress);//connects to the device's address and checks if it's available
                    btSocket = dev.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    //bluetoothAdapter.cancelDiscovery();
                    btSocket.connect();//start connection
                }
                else{
                    showToast("You are not connected");
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                showToast("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                showToast("Connected.");

                isConnected.putBoolean("isConnected",true);
                connectedAddress1.putString("connectedAddress",myAddress);
                try
                {
                    btSocket.getOutputStream().write("S".toString().getBytes());
                }
                catch (IOException e)
                {
                    showToast("Error");
                }
                isBtConnected = true;
            }
            //progress.dismiss();
        }
    }
*/
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    public void openBtActivity() {
        Intent intent = new Intent (this, BtActivity.class);
        startActivity(intent);
    }
    private void driveForward()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("F".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private void noAction()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("S".toString().getBytes());

            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private void driveBackward()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("B".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private void driveToLeft()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("L".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private void lightsOn()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("W".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private void lightsOff()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("w".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private void buzzerOn()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("U".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private void buzzerOff()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("u".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private void driveToRight()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("R".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private String receiveData()
    {

        String s="";
        if (btSocket!=null)
        {
            try
            {

                byte[] inBuffer = new byte[256];
                int bytesRead = btSocket.getInputStream().read(inBuffer);
                //final byte[] temp = new byte [bytesRead];
                //System.arraycopy(inBuffer, 0, temp, 0, bytesRead);
                //int i = 0;
                //while (i< temp.length) {
                 //   dataQueue.add(temp[i]);
                 //   i++;
                //}
               // byte bytesRead2 = dataQueue.pop();
                //s = new String(inBuffer, "ASCII");
                //s = s.substring(0, bytesRead);
                s = new String(inBuffer,0,bytesRead);

            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
        return s;
    }
    private void getMeasurementRequest()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("T".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }private void stopMeasurementRequest()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("X".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }
    private void sendMeasurement(String measurement){
/* trying intent
        Intent i = new Intent(this, CalcActivity.class);
        i.putExtra("MEASUREMENT",measurement);
        startActivity(i);*/


        listOfMeasurements.add(new Measurement(i,measurement));
        tinydb = new TinyDB(this);

        tinydb.putListMeasurements("listobjects",listOfMeasurements);
        i++;

    }
    private class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;
        public View touchedView;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if(touchedView.isEnabled()) {
                    handler.postDelayed(this, normalInterval);
                    clickListener.onClick(touchedView);
                } else {
                    // if the view was disabled by the clickListener, remove the callback
                    handler.removeCallbacks(handlerRunnable);
                    touchedView.setPressed(false);
                    touchedView = null;
                }
            }
        };

        /**
         * @param initialInterval The interval after first click event
         * @param normalInterval The interval after second and subsequent click
         *       events
         * @param clickListener The OnClickListener, that will be called
         *       periodically
         */
        public RepeatListener(int initialInterval, int normalInterval,
                              View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(handlerRunnable, initialInterval);
                    touchedView = view;
                    touchedView.setPressed(true);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(handlerRunnable);
                    touchedView.setPressed(false);
                    touchedView = null;
                    noAction();

                    return true;
            }

            return false;
        }

    }

}

