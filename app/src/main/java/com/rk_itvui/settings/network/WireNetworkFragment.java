package com.rk_itvui.settings.network;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//import android.net.pppoe.PppoeManager;
//import android.net.PppoeManager;
import android.net.wifi.WifiManager;
import android.net.EthernetManager;

import com.rk_itvui.settings.MyNetUtil;
import com.zxy.idersettings.R;
import android.os.SystemProperties;

public class WireNetworkFragment extends Fragment {
//	private PppoeManager mPppoeMgr;
//	private int mPppoeState;
	private View wirenetworksettingLayout;
	private final Handler mHandler;
	private Context mContext;
	private IntentFilter mIntentFilter;
	private String sIpAddress;
	private String sNetmask;
	private String sGateway;
	private String sdns1;
	private String sdns2;
	private String mac;
	private TextView macwire;
	private TextView ipAddr;
	private TextView netMask;
	private TextView gateWay;
	private TextView dns1;
	private TextView dns2;
	private TextView netMode;
	private boolean isConnected = false;
	private EthernetIP ethernetIP;
	private String nullIpInfo = "0.0.0.0";
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			int state = intent.getIntExtra(
//					EthernetManager.EXTRA_ETHERNET_STATE, 0);
			checkNetMode();
		}
	};
	
	public WireNetworkFragment(){
		this.mHandler=null;
	}
	public WireNetworkFragment(Handler handler,Context context){
		this.mHandler=handler;
		this.mContext=context;
	}
	public void resume() {
		mContext.registerReceiver(mReceiver, mIntentFilter);
	}
	public void pause(){
		mContext.unregisterReceiver(mReceiver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		wirenetworksettingLayout = inflater.inflate(
				R.layout.wirenetwork_fragment, container, false);
		mIntentFilter = new IntentFilter();

//		mIntentFilter = new IntentFilter(
//				EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
		mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE"); 
		mIntentFilter.addAction("android.net.wifi.RSSI_CHANGED"); 
		mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); 
		init();
		checkNetMode();
		this.resume();
		return wirenetworksettingLayout;
	}

	void init() {
		ipAddr = (TextView) wirenetworksettingLayout
				.findViewById(R.id.ipAddrValue);
		netMask = (TextView) wirenetworksettingLayout
				.findViewById(R.id.netMaskValue);
		gateWay = (TextView) wirenetworksettingLayout
				.findViewById(R.id.gateWayValue);
		dns1 = (TextView) wirenetworksettingLayout
				.findViewById(R.id.masterDNSValue);
		dns2 = (TextView) wirenetworksettingLayout
				.findViewById(R.id.backupDNSValue);
		netMode = (TextView) wirenetworksettingLayout
				.findViewById(R.id.network_mode);
//		mPppoeMgr = (PppoeManager) mContext.getSystemService(
//				Context.PPPOE_SERVICE);

	    ethernetIP = new EthernetIP();
		ethernetIP.transContext(mContext);
	}

	public void checkNetMode() { //
		
		
//		mPppoeState = mPppoeMgr.getPppoeState();
//		if (PppoeManager.PPPOE_STATE_CONNECTED == mPppoeState) {
//			netMode.setText(R.string.pppoe);
//			isConnected = true;
//			getPppoeInfoFromProperties(); 
//			updateFragment();
//			return;
//		}
		

		if (!ethernetIP.isConnected()) {
			netMode.setText(R.string.disconncect);
			isConnected = false;
			updateFragment();
			return;
		} 
		char ipType;
		if (ethernetIP.isUsingStaticIp()) {
			netMode.setText(R.string.staticIP);
			ipType = 0;
		} else {
			netMode.setText(R.string.dhcp);
			ipType = 1;
		}
		
		this.sIpAddress=ethernetIP.getIPAddress(ipType);
		this.sNetmask = ethernetIP.getNetMask(ipType);
		this.sGateway = ethernetIP.getGateWay(ipType);
		this.sdns1 = ethernetIP.getDNS1(ipType);
		this.sdns2 = ethernetIP.getDNS2(ipType);
		this.sdns1 = MyNetUtil.getEthMac();
		isConnected = true;
		updateFragment();
	}

	public void updateFragment() {
		if (isConnected) {
			ipAddr.setText(sIpAddress);
			netMask.setText(sNetmask);
			gateWay.setText(sGateway);
			dns1.setText(sdns1);
			dns2.setText(sdns2);
			
			mHandler.sendEmptyMessage(1);
		} else {
			ipAddr.setText("");
			netMask.setText("");
			gateWay.setText("");
			dns1.setText("");
			dns2.setText("");
			mHandler.sendEmptyMessage(0);
		}
	}

	public void getPppoeInfoFromProperties() {
		String tempIpInfo;
		// String iface = mEthManager.getEthernetIfaceName();
		tempIpInfo = SystemProperties.get("net.ppp0.local-ip");
		Log.d("net.ppp0.local-ip", "===================================="
				+ tempIpInfo);
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			sIpAddress = tempIpInfo;
		} else {
			sIpAddress = nullIpInfo;
		}
		tempIpInfo = MyNetUtil.getEthMac();
//		tempIpInfo = SystemProperties.get("net.ppp0.mask");
		Log.d("net.ppp0.mask", "===================================="
				+ tempIpInfo);
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			sNetmask = tempIpInfo;
		} else {
			sNetmask = nullIpInfo;
		}
		tempIpInfo = SystemProperties.get("net.ppp0.remote-ip");
		Log.d("net.ppp0.remote-ip", "===================================="
				+ tempIpInfo);
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			sGateway = tempIpInfo;
		} else {
			sGateway = nullIpInfo;
		}

		tempIpInfo = SystemProperties.get("net.ppp0.dns1");
		Log.d("net.ppp0.dns1", "===================================="
				+ tempIpInfo);
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			sdns1 = tempIpInfo;
		} else {
			sdns1 = nullIpInfo;
		}
		tempIpInfo = SystemProperties.get("net.ppp0.dns2");
		Log.d("net.ppp0.dns2", "===================================="
				+ tempIpInfo);
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			sdns2 = tempIpInfo;
		} else {
			sdns2 = nullIpInfo;
		}

	}

	public void getHandlerMethod(Handler handler) {
	//	mHandler = handler;
	}

}
