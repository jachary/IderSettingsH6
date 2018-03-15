package com.rk_itvui.settings.network;
import java.util.ArrayList;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxy.idersettings.R;

public class WifiApFragment extends Fragment {
	
	public static final int OPEN_INDEX = 0;
	public static final int WPA_INDEX = 1;
	public static final int WPA2_INDEX = 2;
	
	View wifiApFragmentLayout;
	private final Handler mhandler;
	ConnectivityManager mCm;
	private String[] mWifiRegexs;
	private Context mContext;
	private IntentFilter mIntentFilter;
	private WifiManager mWifiManager;
	TextView netssid;
	TextView wifiSecurityType;
	TextView wifiApPassword;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WifiManager.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
				handleWifiApStateChanged(intent.getIntExtra(
						WifiManager.EXTRA_WIFI_AP_STATE,
						WifiManager.WIFI_AP_STATE_FAILED));
			} else if (ConnectivityManager.ACTION_TETHER_STATE_CHANGED
					.equals(action)) {
				ArrayList<String> available = intent
						.getStringArrayListExtra(ConnectivityManager.EXTRA_AVAILABLE_TETHER);
				ArrayList<String> active = intent
						.getStringArrayListExtra(ConnectivityManager.EXTRA_ACTIVE_TETHER);
				ArrayList<String> errored = intent
						.getStringArrayListExtra(ConnectivityManager.EXTRA_ERRORED_TETHER);
				updateTetherState(available.toArray(), active.toArray(),errored.toArray());
			}
		//	updatamessage();
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		wifiApFragmentLayout = inflater.inflate(R.layout.wifiap_fragment,
				container, false);
		init();
		this.resume();
		return wifiApFragmentLayout; 
	}
	
	public WifiApFragment(){
		this.mhandler=null;
	}
	public WifiApFragment(Handler handler,Context context){
		this.mhandler=handler;
		this.mContext=context;
	}
	public void init(){
	//	this.mContext=this.getActivity();
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		mCm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifiRegexs = mCm.getTetherableWifiRegexs();
		mIntentFilter = new IntentFilter(
				WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
		mIntentFilter
				.addAction(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		netssid=(TextView)wifiApFragmentLayout.findViewById(R.id.wifiSsidValue);
		wifiSecurityType=(TextView)wifiApFragmentLayout.findViewById(R.id.wifiSecurityType);
		wifiApPassword=(TextView)wifiApFragmentLayout.findViewById(R.id.wifiApPassword); 
		
	}
	public void resume() {
		mContext.registerReceiver(mReceiver, mIntentFilter);
	}

	public void pause() {
		mContext.unregisterReceiver(mReceiver);
	}

	private void updateTetherState(Object[] available, Object[] tethered,
			Object[] errored) {
		boolean wifiTethered = false;
		boolean wifiErrored = false;
		LOGD("updateTetherState~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		for (Object o : tethered) {
			String s = (String) o;
			for (String regex : mWifiRegexs) {
				if (s.matches(regex))
					wifiTethered = true;
			}
		}
		for (Object o : errored) {
			String s = (String) o;
			for (String regex : mWifiRegexs) {
				if (s.matches(regex))
					wifiErrored = true;
			}
		}

		if (wifiTethered) {
			WifiConfiguration wifiConfig = mWifiManager
					.getWifiApConfiguration();
			updateConfigSummary(wifiConfig);
		} else if (wifiErrored) {
			// ((RKSettings)mContext).updateSettingItem(R.string.wifi_tether_checkbox_text,R.string.wifi_error,-1,-1)
		}
	}
	public static int getSecurityTypeIndex(WifiConfiguration wifiConfig) {
		if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return WPA_INDEX;
		} else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
			return WPA2_INDEX;
		}
		return OPEN_INDEX;
	}
	
	void updateConfigSummary(WifiConfiguration wifiConfig) {
		String s =mContext.getString(com.android.internal.R.string.wifi_tether_configure_ssid_default);
		String summary = String.format((wifiConfig == null) ? s : wifiConfig.SSID);
		String password=wifiConfig.preSharedKey;
		Log.d("blb","=========================================updateConfigSummary ="+summary);
		switch(getSecurityTypeIndex(wifiConfig)){
		case WPA_INDEX:
			wifiSecurityType.setText("WPA PSK");
			break;
		case WPA2_INDEX:
			wifiSecurityType.setText("WPA2 PSK");
			break;
		case OPEN_INDEX:
			wifiSecurityType.setText("Open");
			break;
			default:
				wifiSecurityType.setText("");
				break;
		}
		netssid.setText(summary);
		wifiApPassword.setText(password);
	}
	void updataMessage(){
		Log.d("blb","=========================================updataMessage =");
	}
	void LOGD(String message){

		Log.d("",message);
	}
	
	private void handleWifiApStateChanged(int state) {
			if(state== WifiManager.WIFI_AP_STATE_DISABLED)
			{
				//姝ゅ闇�瑕佹竻绌篎ragment鍖哄煙鏁版嵁
				netssid.setText("");
				wifiSecurityType.setText("");
				this.wifiApPassword.setText("");
			}
			this.mhandler.sendEmptyMessage(state);
	}
}

