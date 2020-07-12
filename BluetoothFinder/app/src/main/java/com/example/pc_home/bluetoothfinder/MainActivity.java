package com.example.pc_home.bluetoothfinder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView textView;
    Button button;
    ArrayList<String> devices = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    BluetoothAdapter bluetoothAdapter;
    TextView textViewCounter;
    TextView alert;
    int counter=0;

// when scan is pressed
    public void scanButton(View view){
        textView.setText("  Searching...");
        button.setEnabled(false);
        devices.clear();
        addresses.clear();
        counter = 0;
        textViewCounter.setText("");
        alert.setText("");
        // check before scanning that bleutooth adapter is enabled
        if (!bluetoothAdapter.isEnabled()){
            Toast.makeText(getApplicationContext(), " bluetooth not Enabled", Toast.LENGTH_SHORT).show();

        }else {
            bluetoothAdapter.startDiscovery();
        }
    }
 // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
        //    Log.i("ACtion", action);
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                textView.setText("  Finished!");
                button.setEnabled(true);
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));

                if( !addresses.contains(address)) {
                    addresses.add(address);

                    String deviceString ="";

                    if(name == null || name.equals("")){

                        deviceString = address + " - RSSI " + rssi + "dBm";
                    }
                    else{
                        deviceString = name + " - RSSI " + rssi + "dBm";
                    }

                    devices.add(deviceString);
                    counter++;
                    // number of people for alert
                    if(counter>=5){
                       alert.setText("Warning: the area is crowded");
                    }
                    textViewCounter.setText("Number of nearby devices are " + counter);
                    arrayAdapter.notifyDataSetChanged();
                }


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listView);
        textView = (TextView)findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        textViewCounter = (TextView)findViewById(R.id.counter);
        alert = (TextView)findViewById(R.id.alert);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,devices);
        listView.setAdapter(arrayAdapter);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth

        }
        // Register for broadcasts when a device is discovered.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(bluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(bluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(bluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver,intentFilter);


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(broadcastReceiver);
    }
}
