/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rk_itvui.settings.network.wifi;
import android.content.BroadcastReceiver;
import android.net.wifi.SupplicantState;
import android.content.Context;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.content.Intent;

import com.rk_itvui.settings.developer.ListViewAdapter;
import com.rk_itvui.settings.developer.SettingMacroDefine;
import com.rk_itvui.settings.network.wifi.Wifi_setting;
import com.zxy.idersettings.R;
public class Wifi_Enabler  {
    private final Context mContext; 
    private final WifiManager mWifiManager;
    private final IntentFilter mIntentFilter;
    private final Handler mHandler;
	// 鐢ㄤ簬琛ㄦ槑wifi鏄惁宸叉墦寮� ,true:wifi鎵撳紑锛� false:wifi娌℃湁鎵撳紑
	private boolean mOpen = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
			LOGD("BroadcastReceiver onReceive(), action = "+action);
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) 
			{
                handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
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
    private void LOGD(String msg)
	{
		if(true)
			Log.d("Wifi Enabler",msg);
	}
	
    public Wifi_Enabler(Context context,Handler handler) {
        mContext = context;

		mHandler = handler;
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

	public void getWifiDefault()
	{
		boolean bool = (Settings.Secure.getInt(mContext.getContentResolver(), 
						Settings.Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON, 0) == 1);
		int hint = (bool?R.string.open:R.string.off);
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
		boolean bool = (Settings.Secure.getInt(mContext.getContentResolver(), 
						Settings.Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON, 0) == 1);
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
		boolean enable = !mOpen;
		int wifiApState = mWifiManager.getWifiApState();
        if (enable && ((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) ||
                (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED))) 
        {
            mWifiManager.setWifiApEnabled(null, false);
        }

		if(mWifiManager.setWifiEnabled(enable))
		{
			mOpen = enable;
			String open = mContext.getResources().getString(R.string.turn_on);
			//upDateWifiStatus(open);
		}
		else
		{
			mHandler.sendEmptyMessage(10000); //鎵撳紑澶辫触
		}
	}

	public void onNetWorkNotificaiton()
	{
		boolean bool = (Settings.Secure.getInt(mContext.getContentResolver(), 
						Settings.Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON, 0) == 1);
		bool = !bool;
		Settings.Secure.putInt(mContext.getContentResolver(), 
					Settings.Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON,
					bool ? 1 : 0);
		int hint = (bool?R.string.open:R.string.off);
		mHandler.sendEmptyMessage(SettingMacroDefine.upDateListView);	
	}

    private void handleWifiStateChanged(int state) {
		LOGD("handleWifiStateChanged(),state = "+state);	
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
        String text = null;
        if (state != null ) 
		{
            WifiInfo info = mWifiManager.getConnectionInfo();
            if (info != null) 
			{
				if(info.getSSID() != null)
		   			text = Summary.get(mContext, info.getSSID(),state);
				else
					text = mContext.getString(R.string.turn_on);
		   		
				Log.d("Wifi_Enabler","info.getSSID() = "+info.getSSID()+",text = "+text);
		
				return ;
            }
        }
    }
   
	public void get_wifisetting()
	{

	}
	
	public boolean getCurrentState()
	{
		mOpen=mWifiManager.isWifiEnabled();
		if(mOpen)
		{		
		

			return true;
		}
		else
		{
			return false;
		}
	}
}