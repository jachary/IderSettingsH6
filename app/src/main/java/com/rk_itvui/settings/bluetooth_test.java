package com.rk_itvui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rk_itvui.settings.bluetooth.LocalBluetoothDevice;
import com.zxy.idersettings.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.rk_itvui.settings.LogUtils.LOGD;

/**
 * Created by guoliang.wgl on 18/3/19.
 */
public class bluetooth_test extends Activity {
    private String SUBTAG = "bluetooth_test" ;
    private List<LocalBluetoothDevice> pairedList;
    private List<LocalBluetoothDevice> availableList;
    private LocalBluetoothDevice tmpDevice;
//    private Button mButton ;
//    private ListView mListView;
    private  boolean mStatus ;
    private com.rk_itvui.settings.adapter.BluetoothAdapter availableAdapter,pairedAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private IntentFilter  mIntentFilter;
    private BroadcastReceiver mReceiver;
    public static BluetoothSocket btSocket;
    private String[] data;

    private final int CANCLE_TYPE_DIALOG=0;
    private final int PAIRED_TYPE_DIALOG=1;
    private Switch bluetooth_switch;
    private TextView tipTextView;
    private LinearLayout bluetooth_list;
    private ListView paired_devices_listview;
    private ListView available_devices_listview;

    private String currentBluetoothMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.bluetoothtest);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bluetooth_setting_layout);
        initView();
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
                    availableList.add(tmpDevice);
                    LOGD(SUBTAG,"--availableList  size -----"+availableList.size());
                    availableAdapter.notifyDataSetChanged();
                    //notifyAll();
                    LOGD(SUBTAG,"device name : "+ device.getName()+ ";device address: " + device.getAddress());
                    /*test*/
                    if(device.getAddress().equals("4C:49:E3:01:EE:1A")){
//                        Dobond(device);
                    }

                }else if(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)){

                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,-1);
                    String msg = null;
                    switch (state) {
                        case BluetoothAdapter.STATE_CONNECTING:
                            msg = "connecting...";
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            msg = "connected...";
                            //doDisCovery();
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

//        mButton = (Button) findViewById(R.id.bluetooth);
        if(mStatus){
            bluetooth_switch.setChecked(true);
        }else {
            bluetooth_switch.setChecked(false);
        }
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mStatus){
//                    LOGD(SUBTAG,"Bluetooth will disable ");
//                    mBluetoothAdapter.disable();
//                }else{
//                    mBluetoothAdapter.enable();
//                    LOGD(SUBTAG,"Device name is :"+ mBluetoothAdapter.getName() + "Device address is :" + mBluetoothAdapter.getAddress());
//
//                    //mBluetoothAdapter.startDiscovery();
//
//                }
//            }
//        });

    }



    public void getBondedDevice(){
        Set<BluetoothDevice> bondedDevice = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice d : bondedDevice){
            LOGD(SUBTAG,"device name : "+ d.getName()+ ";device address: " + d.getAddress());
            LocalBluetoothDevice device=new LocalBluetoothDevice(d.getName(),d.getAddress());
            //判断设备是否已经配对，否则添加到已配对设备列表
            if(d.getBondState() != BluetoothDevice.BOND_BONDED){
                if (!availableList.contains(device)){
                    availableList.add(new LocalBluetoothDevice(d.getName(),d.getAddress()));
                }
            }else{
                if (!pairedList.contains(device)){
                    pairedList.add(new LocalBluetoothDevice(d.getName(),d.getAddress()));
                }
            }
        }
        availableAdapter.notifyDataSetChanged();
        pairedAdapter.notifyDataSetChanged();
    }

    private void initView() {
        availableList=new ArrayList<LocalBluetoothDevice>();
        pairedList=new ArrayList<LocalBluetoothDevice>();

        available_devices_listview = (ListView) findViewById(R.id.avaiable_devices_listview);
        paired_devices_listview = (ListView) findViewById(R.id.paired_devices_listview);
        tipTextView = (TextView) findViewById(R.id.tip_text);
        bluetooth_list = (LinearLayout) findViewById(R.id.devices_list);
        availableAdapter = new com.rk_itvui.settings.adapter.BluetoothAdapter(this,availableList);
        pairedAdapter = new com.rk_itvui.settings.adapter.BluetoothAdapter(this,pairedList);
        paired_devices_listview.setAdapter(pairedAdapter);
        available_devices_listview.setAdapter(availableAdapter);
        paired_devices_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                showDialog("取消配对",PAIRED_TYPE_DIALOG);
            }
        });
        available_devices_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                currentBluetoothMac = availableList.get(position).getAddress();
                showDialog("开始配对",CANCLE_TYPE_DIALOG);

            }
        });
        bluetooth_switch = (Switch) findViewById(R.id.bluetooth_on_off);
        bluetooth_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //蓝牙打开
                    showToast("蓝牙打开...");
                    LOGD(SUBTAG,"Device name is :"+ mBluetoothAdapter.getName() + "Device address is :" + mBluetoothAdapter.getAddress());
                    boolean b = mBluetoothAdapter.enable();
                    boolean b1=mBluetoothAdapter.startDiscovery();
                    LOGD(SUBTAG,"蓝牙打开-mBluetoothAdapter.enable()-"+b);
                    LOGD(SUBTAG,"mBluetoothAdapter.startDiscovery();-"+b1);
                    bluetooth_list.setVisibility(View.VISIBLE);
                    tipTextView.setVisibility(View.GONE);
                    bluetooth_switch.setBackgroundResource(R.drawable.bluetooth_on);
                }else{
                    //蓝牙关闭
                    showToast("蓝牙关闭...");
                    LOGD(SUBTAG,"Bluetooth will disable ");
                    mBluetoothAdapter.disable();
                    bluetooth_list.setVisibility(View.GONE);
                    tipTextView.setVisibility(View.VISIBLE);
                    bluetooth_switch.setBackgroundResource(R.drawable.bluetooth_off);
                }
            }
        });
    }

    private void showDialog(String tip, final int type) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(tip).setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                if (type==PAIRED_TYPE_DIALOG) {
                    //开始配对...
                    showToast("开始配对...");
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(currentBluetoothMac);
                    if (device.getAddress().equals(currentBluetoothMac)){
                        Dobond(device);
                    }
                }else if(type==CANCLE_TYPE_DIALOG){
                    //取消配对...
                    showToast("取消配对...");
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(currentBluetoothMac);
                    device.removeBond();
                }
                getBondedDevice();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).create().show();
    }


    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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


