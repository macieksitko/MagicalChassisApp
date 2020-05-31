package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;



public class BtActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "BtActivity";
    public static final String EXTRA_ADDRESS = "";


    ListView listView;
    Switch switchBtn;
    TextView searchBtn, pairedBtn, switchState;
    ImageView image;
    ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private DeviceArrayAdapter btArrayAdapter;
    private DeviceArrayAdapter btArrayAdapter2;
    //private  ArrayAdapter<String> btArrayAdapter2;
    private ArrayList<Device> mPairedList = new ArrayList<>();
    private ArrayList<Device> mFoundList = new ArrayList<>();
    private ArrayList<String> temp = new ArrayList<>();
    String stringOn, stringOff;
    boolean isConnected=false;
    String connectedAddress="";
    TinyDB tinyDBisConnected,tinyDBconnectedAddress,tinyDBswappedStatus;
    Device temp1; //temporary connected object for mPairedList
    BluetoothDevice temp2; //temporary connected object for mDeviceList
    boolean swappedStatus;
    int k = 1; //counter of paired devices list loop
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        listView = (ListView) findViewById(R.id.devList);
        switchBtn = (Switch) findViewById(R.id.switchBtn);
        searchBtn = (TextView) findViewById(R.id.searchBtn);
        pairedBtn = (TextView) findViewById(R.id.pairedBtn);
        image = (ImageView) findViewById(R.id.imageView2);
        switchState = (TextView) findViewById(R.id.switchState);
        stringOn = getResources().getString(R.string.switchStateOn);
        stringOff = getResources().getString(R.string.switchStateOff);
        swappedStatus = false;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {

        super.onStart();
        btArrayAdapter = new DeviceArrayAdapter(BtActivity.this, mPairedList);

        tinyDBswappedStatus = new TinyDB(this);
        tinyDBisConnected = new TinyDB(BtActivity.this);
        tinyDBconnectedAddress= new TinyDB(BtActivity.this);

        //registering receiver1 and receiver2
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver1, filter);

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver2, filter1);

        //btArrayAdapter2 = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, mDeviceList);
        // listView.setAdapter(btArrayAdapter2);


        //Set bluetooth adapter and check if it supports bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            showToast("Device doesnt support Bluetooth");
        } else {
            //

            // Quick permission check
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }

            //switchBtn state onCreate activity

            if (bluetoothAdapter.isEnabled()) {
                listView.setAlpha(1.0f);
                pairedBtn.setAlpha(1.0f);
                searchBtn.setAlpha(1.0f);
                image.setAlpha(255);

                switchBtn.setChecked(true);
                listView.setEnabled(true);
                searchBtn.setEnabled(true);
                pairedBtn.setEnabled(true);
                image.setEnabled(true);

                switchState.setText(stringOn);
            } else {
                listView.setAlpha(0.3f);
                pairedBtn.setAlpha(0.3f);
                searchBtn.setAlpha(0.3f);
                image.setAlpha(45);

                switchBtn.setChecked(false);
                listView.setEnabled(false);
                searchBtn.setEnabled(false);
                pairedBtn.setEnabled(false);
                image.setEnabled(false);
                switchState.setText(stringOff);
            }


            switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (buttonView.isChecked()) {
                        listView.setAlpha(1.0f);
                        pairedBtn.setAlpha(1.0f);
                        searchBtn.setAlpha(1.0f);
                        image.setAlpha(255);
                        listView.setEnabled(true);
                        switchBtn.setEnabled(true);
                        searchBtn.setEnabled(true);
                        pairedBtn.setEnabled(true);
                        image.setEnabled(true);
                        switchState.setText(stringOn);
                        turnOnBluetooth();
                    } else {
                        listView.setAlpha(0.3f);
                        pairedBtn.setAlpha(0.3f);
                        searchBtn.setAlpha(0.3f);
                        image.setAlpha(45);
                        switchState.setText(stringOff);
                        turnOffBluetooth();
                    }
                }
            });

            //onItemClick event
            listView.setOnItemClickListener(BtActivity.this);
            //Paired devices
            pairedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!bluetoothAdapter.isEnabled()) {
                        showToast("BT is not enabled, cannot pair");
                    } else {

                        connectedAddress = tinyDBconnectedAddress.getString("address");
                        if(!mDeviceList.isEmpty()) mDeviceList.clear();
                        if (btArrayAdapter != null) btArrayAdapter.clear();
                        if (btArrayAdapter2 != null) btArrayAdapter2.clear();
                        showToast("BT is enabled, you can pair");
                        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                        // If there are paired devices
                        if (pairedDevices.size() > 0) {

                            // Loop through paired devices
                            for (BluetoothDevice device : pairedDevices) {
                                mDeviceList.add(device);
                                // Add the name and address to an array adapter to show in a ListView
                                if (tinyDBisConnected.getBoolean("isConnected") && device.getAddress().equals(connectedAddress)) {
                                    temp1 = new Device(device.getName(),1);
                                    mPairedList.add(temp1);
                                    temp2 = device;
                                }
                                else mPairedList.add(new Device(device.getName(),0));
                                k++;
                            }
                        }else{
                            showToast("Any paired devices yet");
                        }

                        //if a device is already connected, show it on the top
                        if(tinyDBswappedStatus.getBoolean("isSwapped")) swapConnectedToTheTop();
                        k = 0;
                        listView.setAdapter(btArrayAdapter);
                    }

                }
            });



            //Discovered devices
            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!bluetoothAdapter.isEnabled()) {
                        showToast("BT is not enabled, cannot search for devices");
                    } else {

                        showToast("Registered...");
                        if (btArrayAdapter != null) btArrayAdapter.clear();
                        if (btArrayAdapter2 != null) btArrayAdapter2.clear();


                        bluetoothAdapter.startDiscovery();
                        showToast("Discovering...");
                        if(!mDeviceList.isEmpty()) mDeviceList.clear();
                    }
                }
            });

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        //btArrayAdapter.notifyDataSetChanged();
        //listView.setAdapter(btArrayAdapter);
    }
    private void turnOffBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            showToast("BT disabled");
        }
    }
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.

    private final BroadcastReceiver receiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.


                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device);
                if(device!=null && device.getName()!=null) {
                    //mDeviceList.add(device);
                    mFoundList.add(new Device(device.getName(),0));
                    btArrayAdapter2 = new DeviceArrayAdapter(BtActivity.this, mFoundList);
                    btArrayAdapter2.notifyDataSetChanged();

                    showToast(device.getName() + "\n" + device.getAddress());
                }
            }
            listView.setAdapter(btArrayAdapter2);
        }
    };
    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    showToast("BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    showToast("BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    showToast("BroadcastReceiver: BOND_NONE.");
                }
            }


        }
    };

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void turnOnBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            showToast("BT enabled");
        }
    }

        @Override
        public void onStop () {
            super.onStop();
            unregisterReceiver(receiver1);
            unregisterReceiver(receiver2);
        }

        @Override
        public void onItemClick (AdapterView < ? > parent, View view, int i, long id){
            bluetoothAdapter.cancelDiscovery();
            String deviceAddress = mDeviceList.get(i).getAddress();
            String deviceName = mDeviceList.get(i).getName();
            swapConnectedToTheTop();



            //scrolling clicked item to top position of view
            //listView.smoothScrollToPositionFromTop(i,0,500);
            //listView.setSelection(i);

            swappedStatus = true;
            tinyDBswappedStatus.putBoolean("isSwapped", swappedStatus);



            //create the bond.
            //NOTE: Requires API 17+, this is JellyBean
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {

                mDeviceList.get(i).createBond();
                showToast("Bonding..");
            }
            Intent in = new Intent(BtActivity.this, DriveActivity.class);
            //Change the activity.
            TinyDB tinyDB1 = new TinyDB(this);
            tinyDB1.putString("address",deviceAddress);
            //in.putExtra(EXTRA_ADDRESS, deviceAddress);//this will be received at Drive (class) Activity
            startActivity(in);

        }
        private void swapConnectedToTheTop(){
            mPairedList.remove(temp1);
            mPairedList.add(0,temp1);
            mDeviceList.remove(temp2);
            mDeviceList.add(0,temp2);
        }
}



