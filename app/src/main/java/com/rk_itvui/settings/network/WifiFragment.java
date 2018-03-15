package com.rk_itvui.settings.network;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.net.InetAddress;

import com.zxy.idersettings.R;

public class WifiFragment extends Fragment {

	private Context mContext;
	private WifiManager mWifiManager;
	private IntentFilter mIntentFilter;

	private final Handler parentHandler;
	private View wifiFragmentLayout;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			LOGD("BroadcastReceiver onReceive(), action = " + action);
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				handleWifiStateChanged(intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_UNKNOWN));
			} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION
					.equals(action)) {
				handleStateChanged(WifiInfo
						.getDetailedStateOf((SupplicantState) intent
								.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
			} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
				handleStateChanged(((NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO))
						.getDetailedState());
			}
		}
	};

	public void resume() {
		mContext.registerReceiver(mReceiver, mIntentFilter);
	}

	public void pause() {
		mContext.unregisterReceiver(mReceiver);
	}

	private void LOGD(String msg) {
		if (true)
			Log.d("WifiEnabler", msg);
	}

	public WifiFragment(Handler handler, Context context) {
		parentHandler = handler;
		this.mContext = context;
	}

	public void init() {
		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		mIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		resume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		wifiFragmentLayout = inflater.inflate(R.layout.wifi_fragment,
				container, false);
		return wifiFragmentLayout;
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			int hint;
			boolean mOpen;
			String netStateInfo;

			switch (msg.what) {
			case WifiManager.WIFI_STATE_ENABLING:
				mOpen = true; // 鎵撳紑涓�
				hint = R.string.wifi_starting;
				break;

			case WifiManager.WIFI_STATE_ENABLED:
				mOpen = true; // 鎵撳紑鎴愬姛
				hint = R.string.turn_on;
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				mOpen = false; // 鍏抽棴涓�
				hint = R.string.wifi_stopping;
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				mOpen = false; // 宸插叧闂�
				hint = R.string.turn_off;
				break;
			default:
				mOpen = false;
				hint = R.string.wifi_error;
				break;
			}
			// parentHandler.sendEmptyMessage(msg.what);
		}
	};

	private void handleWifiStateChanged(int state) {
		parentHandler.sendEmptyMessage(state);
	}

	private void handleStateChanged(NetworkInfo.DetailedState state) {
		// WifiInfo is valid if and only if Wi-Fi is enabled.
		// Here we use the state of the check box as an optimization.
		String text = null;
		if (state != null) {
			WifiInfo info = mWifiManager.getConnectionInfo();
			if (info != null) {
				if (info.getSSID() != null) {
					/*text = Summary.get(mContext, info.getSSID(), state);
					Log.d("Wifi_Enabler blb",
							"===========================================if"
									+ state);*/
				} else
					text = mContext.getString(R.string.turn_on);

				Log.d("Wifi_Enabler", "info.getSSID() = " + info.getSSID()
						+ ",text = " + text);
				// mOpen = true;
				if(text!=null)
				upDateFragmentState(text);
				upDateNetInfo();
				return;
			}
		}
	}

	private void upDateFragmentState(String text) {
		TextView wifiState;
		wifiState = (TextView) wifiFragmentLayout
				.findViewById(R.id.network_state);
		wifiState.setText(text);
	}

	private void upDateNetInfo() {
		WifiInfo wifiinfo = this.mWifiManager.getConnectionInfo();
		TextView tipAddr;
		TextView tlinkSpeed;
		TextView tmacAddr;
		tipAddr = (TextView) wifiFragmentLayout.findViewById(R.id.ipAddrValue);
		tlinkSpeed = (TextView) this.wifiFragmentLayout
				.findViewById(R.id.linkSpeedValue);
		tmacAddr = (TextView) this.wifiFragmentLayout
				.findViewById(R.id.macAddressValue);
		String ipAddr = intToIp(wifiinfo.getIpAddress());
		int linkSpeed = wifiinfo.getLinkSpeed();
		String macAddr = wifiinfo.getMacAddress();

		tipAddr.setText(ipAddr);
		tlinkSpeed.setText(Integer.toString(linkSpeed) + "Mbs");
		tmacAddr.setText(macAddr);

	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	private void getCurState() {

	}

}
