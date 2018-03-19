package com.rk_itvui.settings;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.rk_itvui.settings.bluetooth.LocalBluetoothDevice;
import com.zxy.idersettings.R;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.rk_itvui.settings.LogUtils.LOGD;

/**
 * Created by guoliang.wgl on 18/3/19.
 */
public class bluetooth_test extends Activity {
    private String SUBTAG = "bluetooth_test" ;
    private List<LocalBluetoothDevice> mDevices;
    private LocalBluetoothDevice tmpDevice;
    Button mButton ;
    ListView mListView;
    private  boolean mStatus ;
    private BluetoothAdapter mBluetoothAdapter;
    private IntentFilter  mIntentFilter;
    private BroadcastReceiver mReceiver;
    public static BluetoothSocket btSocket;
    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetoothtest);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                LOGD(SUBTAG,"BroadcastReceiver onReceive(), action = "+action);
                if(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)){

                    LOGD(SUBTAG,"Bluetooth  connection state changed");
                }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    String msg = null;
                    switch (state) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            msg = "turning on";
                            break;
                        case BluetoothAdapter.STATE_ON:
                            msg = "on";
                            doDisCovery();
                            mStatus = true;
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            msg = "turning off";
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            msg = "off";
                            mStatus = false;
                            break;
                    }


                    LOGD(SUBTAG, "Bluetooth status changed,state is : " + msg);
                }else if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    LOGD(SUBTAG,"Bluetooth found  devices");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    tmpDevice = new LocalBluetoothDevice(device.getName(),device.getAddress());
                    mDevices.add(tmpDevice);
                    notifyAll();
                    LOGD(SUBTAG,"device name : "+ device.getName()+ ";device address: " + device.getAddress());
                    /*test*/

                    if(device.getAddress().equals("4C:49:E3:01:EE:1A")){
                        Dobond(device);
                    }
                    //getBondedDevice();
                }else if(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)){

                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,-1);
                    String msg = null;
                    switch (state) {
                        case BluetoothAdapter.STATE_CONNECTING:
                            msg = "connecting...";
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            msg = "connected...";
                            doDisCovery();
                            mStatus = true;
                            break;
                        case BluetoothAdapter.STATE_DISCONNECTING:
                            msg = "disconnecting...";
                            break;
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            msg = "disonnected...";
                            mStatus = false;
                            break;
                    }


                    LOGD(SUBTAG, "Bluetooth  connect status changed,state is : " + msg);
                }
            }
        };



        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);  //发现新设备
        mIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  //绑定状态改变
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);  //开始扫描
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //结束扫描
        mIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);  //连接状态改变
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver,mIntentFilter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mStatus = mBluetoothAdapter.isEnabled();

        mButton = (Button) findViewById(R.id.bluetooth);
        if(mStatus){
            mButton.setText("关闭蓝牙");
        }else {
            mButton.setText("打开蓝牙");
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStatus){
                    LOGD(SUBTAG,"Bluetooth will disable ");
                    mBluetoothAdapter.disable();
                }else{
                    mBluetoothAdapter.enable();
                    LOGD(SUBTAG,"Device name is :"+ mBluetoothAdapter.getName() + "Device address is :" + mBluetoothAdapter.getAddress());

                    //mBluetoothAdapter.startDiscovery();

                }
            }
        });

        mListView = (ListView) findViewById(R.id.devicelist);

    }


    public void getBondedDevice(){
        Set<BluetoothDevice> bondedDevice = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice d : bondedDevice){
            LOGD(SUBTAG,"device name : "+ d.getName()+ ";device address: " + d.getAddress());
            //判断设备是否已经配对，否则添加到已配对设备列表
            if(d.getBondState() != BluetoothDevice.BOND_BONDED){

            }else{

            }
        }

    }

    public void doDisCovery(){
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();

        }
        LOGD(SUBTAG,"discovery state:" + mBluetoothAdapter.isDiscovering());

        mBluetoothAdapter.startDiscovery();

        LOGD(SUBTAG,"discovery state:" + mBluetoothAdapter.isDiscovering());
    }


    public void Dobond(BluetoothDevice mRemoteDevice) {
        if(mRemoteDevice.getBondState() == BluetoothDevice.BOND_NONE){
            mRemoteDevice.createBond();
        }else if(mRemoteDevice.getBondState() == BluetoothDevice.BOND_BONDED){
            connect(mRemoteDevice);
        }



    }


    private void connect(BluetoothDevice btDev) {
//        UUID uuid = UUID.fromString(SPP_UUID);
//        try {
//            btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
//            LOGD(SUBTAG, "start connecting ...");
//            btSocket.connect();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBluetoothAdapter.cancelDiscovery();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}


