package com.rk_itvui.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.*;
import android.util.Log;

import com.rk_itvui.settings.developer.SettingMacroDefine;
import com.rk_itvui.settings.network.wifi.Summary;
import com.zxy.idersettings.R;

import java.util.Set;

import static com.rk_itvui.settings.LogUtils.LOGD;

/**
 * Created by guoliang.wgl on 18/3/18.
 */
public class Bluetooth_Enabler {

    private final Context mContext;
    private final IntentFilter mIntentFilter;
    private final Handler mHandler;
    private boolean mOpen = false;
    private BluetoothAdapter btAdapter;
    private String SUBTAG = "Bluetooth_Enabler";
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            LOGD(SUBTAG,"BroadcastReceiver onReceive(), action = "+action);
            if(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)){

                LOGD(SUBTAG,"Bluetooth  connection state changed");
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){

                LOGD(SUBTAG,"Bluetooth status changed:");
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                LOGD(SUBTAG,"Bluetooth found  devices");
                getBondedDevice();
            }
            else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action))
            {
                handleStateChanged(WifiInfo.getDetailedStateOf((SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
            }
            else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action))
            {
                handleStateChanged(((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState());
            }
        }

    };


    public Bluetooth_Enabler(Context context,Handler handler) {
        mContext = context;

        mHandler = handler;
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);  //发现新设备
        mIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  //绑定状态改变
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);  //开始扫描
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //结束扫描
        mIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);  //连接状态改变
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);  //蓝牙开关状态改变
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        mOpen = btAdapter.isEnabled();
    }

    public void getWifiDefault()
    {
        boolean bool = (android.provider.Settings.Secure.getInt(mContext.getContentResolver(),
                android.provider.Settings.Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON, 0) == 1);
        int hint = (bool? R.string.open:R.string.off);
        if(mOpen)
        {
            mHandler.sendEmptyMessage(100);//wifi鎵撳紑浜�
        }
        else
        {
            mHandler.sendEmptyMessage(1000); //wifi鍏抽棴浜�
        }
    }

    private void upDateWifiStatus(String title)
    {
        boolean bool = (android.provider.Settings.Secure.getInt(mContext.getContentResolver(),
                android.provider.Settings.Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON, 0) == 1);
        int hint = (bool?R.string.open:R.string.off);

        if(mOpen)
        {
        }
        else
        {
        }
    }

    public void resume() {
        mContext.registerReceiver(mReceiver, mIntentFilter);
    }

    public void pause() {
        mContext.unregisterReceiver(mReceiver);
    }

    public void onWiFiClick()
    {

        if(btAdapter.isEnabled()){
            btAdapter.disable();
            mOpen = false;
            btAdapter.startDiscovery();
        }else{
            btAdapter.enable();
            mOpen = true;
            btAdapter.startDiscovery();
        }
        LOGD(SUBTAG,"bt status is:" + btAdapter.isEnabled());
//        int wifiApState = mWifiManager.getWifiApState();
//        if (enable && ((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) ||
//                (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED)))
//        {
//            mWifiManager.setWifiApEnabled(null, false);
//        }
//
//        if(mWifiManager.setWifiEnabled(enable))
//        {
//            mOpen = enable;
//            String open = mContext.getResources().getString(R.string.turn_on);
//            //upDateWifiStatus(open);
//        }
//        else
//        {
//            mHandler.sendEmptyMessage(10000); //鎵撳紑澶辫触
//        }
    }

    public void onNetWorkNotificaiton()
    {
        boolean bool = (android.provider.Settings.Secure.getInt(mContext.getContentResolver(),
                android.provider.Settings.Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON, 0) == 1);
        bool = !bool;
        android.provider.Settings.Secure.putInt(mContext.getContentResolver(),
                android.provider.Settings.Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON,
                bool ? 1 : 0);
        int hint = (bool?R.string.open:R.string.off);
        mHandler.sendEmptyMessage(SettingMacroDefine.upDateListView);
    }

    private void handleWifiStateChanged(int state) {
        LOGD(SUBTAG,"handleWifiStateChanged(),state = "+state);
        this.mHandler.sendEmptyMessage(state);
        int hint = 0;
        switch (state)
        {
            case WifiManager.WIFI_STATE_ENABLING:
                mOpen = true;  //鎵撳紑涓�
                hint = R.string.wifi_starting;
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                mOpen = true; //鎵撳紑鎴愬姛
                hint = R.string.turn_on;
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                mOpen = false; //鍏抽棴涓�
                hint = R.string.wifi_stopping;
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                mOpen = false; //宸插叧闂�
                hint = R.string.turn_off;
                break;
            default:
                mOpen = false;
                hint = R.string.wifi_error;
                break;
        }

    }

    private void handleStateChanged(NetworkInfo.DetailedState state) {
        // WifiInfo is valid if and only if Wi-Fi is enabled.
        // Here we use the state of the check box as an optimization.
//        String text = null;
//        if (state != null )
//        {
//            WifiInfo info = mWifiManager.getConnectionInfo();
//            if (info != null)
//            {
//                if(info.getSSID() != null)
//                    text = Summary.get(mContext, info.getSSID(),state);
//                else
//                    text = mContext.getString(R.string.turn_on);
//
//                Log.d("Wifi_Enabler","info.getSSID() = "+info.getSSID()+",text = "+text);
//
//                return ;
//            }
//        }
    }

    public void get_wifisetting()
    {

    }

    public boolean getCurrentState()
    {
        mOpen=btAdapter.isEnabled();
        if(mOpen)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void getBondedDevice(){
        Set<BluetoothDevice> bondedDevice = btAdapter.getBondedDevices();
        for(BluetoothDevice d : bondedDevice){
                LOGD(SUBTAG,"device name : "+ d.getName()+ ";device address: " + d.getAddress());
                //判断设备是否已经配对，否则添加到已配对设备列表
                if(d.getBondState() != BluetoothDevice.BOND_BONDED){

                }else{

                }
        }

    }
}
