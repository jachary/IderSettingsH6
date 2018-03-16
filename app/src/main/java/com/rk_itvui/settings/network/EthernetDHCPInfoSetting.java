package com.rk_itvui.settings.network;

import com.rk_itvui.settings.FullScreenActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.zxy.idersettings.R;
//for 5.0
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
import android.net.wifi.WifiManager;
import android.net.StaticIpConfiguration;
import android.net.NetworkUtils;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.preference.ListPreference;

public class EthernetDHCPInfoSetting extends FullScreenActivity {

	public TextView ipaddr;
	public TextView netmask;
	public TextView gateway;
	public TextView dns1;
	public TextView dns2;
	public TextView netstate;
	public LinearLayout dhcpContent;
	public LinearLayout dhcpFailed;
	public LinearLayout dhcpConnecting;
	public TextView dhcpStateContent;
	boolean isFirstDHCP=false;
	public EthernetIP ethernetIP = new EthernetIP();
	private IntentFilter mIntentFilter;
	EthernetManager mEthMgr;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
	    public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
//                if (action.equals(EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
//                	Log.i("log", "============================"+EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
//                	/*接收到以太网状态改变的广播*/
//                  int state = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, 0);
//                  Log.i("log", "-----------------"+state);
//		  getEthInfoFromDhcp(state);
//	        }
	    }
	};

	public void getEthInfoFromDhcp(int state) {
		Log.i("EthernetDHCPInfoSetting",Thread.currentThread().getStackTrace()[2].getMethodName()+"state="+state);
		switch (state) {
//		case EthernetManager.ETHER_STATE_CONNECTING:
//			Log.i("log", "+++++++++++++++++"+EthernetManager.ETHER_STATE_CONNECTING);
//			netstate.setText(R.string.dhcp_connecting);
//			dhcpConnecting.setVisibility(View.VISIBLE);
//			dhcpContent.setVisibility(View.GONE);
//			dhcpFailed.setVisibility(View.GONE);
//
//			break;
//		case EthernetManager.ETHER_STATE_DISCONNECTED:
//			/*
//			if (mEthMgr.getEthernetCarrierState() ==0) {
//				netstate.setText(R.string.dhcp_disconnected);
//				dhcpConnecting.setVisibility(View.GONE);
//				dhcpContent.setVisibility(View.GONE);
//				dhcpFailed.setVisibility(View.VISIBLE);
//				return;
//			}*/
//			{
//			netstate.setText(R.string.dhcp_disconnected);
//				dhcpConnecting.setVisibility(View.GONE);
//				dhcpContent.setVisibility(View.GONE);
//				dhcpFailed.setVisibility(View.VISIBLE);
//			this.dhcpStateContent.setText(R.string.dhcp_notconfig);
//			updateNetInfo(true);
//			}
//			break;
//		case EthernetManager.ETHER_STATE_CONNECTED: // 杩炴帴鎴愬姛浠ュ悗锛屽鏋淧PPOE宸茶繛鎺ワ紝闇�瑕佹柇寮�
//			this.dhcpStateContent.setText(R.string.dhcp_state_connected);
//			netstate.setText(R.string.dhcp_connected);
//			dhcpConnecting.setVisibility(View.GONE);
//			dhcpContent.setVisibility(View.VISIBLE);
//			dhcpFailed.setVisibility(View.GONE);
//			updateNetInfo(true);
///*
//			PppoeManager mPppoeMgr = (PppoeManager) getSystemService(Context.PPPOE_SERVICE);
//			int mPppoeState;
//			mPppoeState = mPppoeMgr.getPppoeState();
//			if (mPppoeState == PppoeManager.PPPOE_STATE_CONNECTED
//					|| mPppoeState == PppoeManager.PPPOE_STATE_CONNECTING) { // 濡傛灉褰撳墠涓鸿繛鎺ョ姸鎬侊紝鎸夐挳涓烘柇寮�
//				mPppoeMgr.stopPppoe();
//			}*/
//			// netDialog("缃戠粶宸茶繛鎺�");
//			break;
		}
	}

	public void updateNetInfo(boolean state) {
		Log.i("EthernetDHCPInfoSetting",Thread.currentThread().getStackTrace()[2].getMethodName());
		if (state) {
			Log.i("log", "+++++++++++++++++"+state);
			char DHCP = 1;
			ipaddr.setText(ethernetIP.getIPAddress(DHCP));
			netmask.setText(ethernetIP.getNetMask(DHCP));
			gateway.setText(ethernetIP.getGateWay(DHCP));
			dns1.setText(ethernetIP.getDNS1(DHCP));
			dns2.setText(ethernetIP.getDNS2(DHCP));
		} else { // 缃┖
			Log.i("log", "+++++++++++++++++-----------------nostate");
			ipaddr.setText("");
			netmask.setText("");
			gateway.setText("");
			dns1.setText("");
			dns2.setText("");
		}
	}

	public void findView() {
		netstate = (TextView) findViewById(R.id.netState);
		ipaddr = (TextView) findViewById(R.id.ipAddrValue);
		netmask = (TextView) findViewById(R.id.netMaskValue);
		gateway = (TextView) findViewById(R.id.gateWayValue);
		dns1 = (TextView) findViewById(R.id.dns1Value);
		dns2 = (TextView) findViewById(R.id.dns2Value);
		dhcpContent = (LinearLayout) findViewById(R.id.dhcp_content);
		dhcpFailed = (LinearLayout) findViewById(R.id.dhcp_failed);
		dhcpConnecting = (LinearLayout) findViewById(R.id.dhcp_connecting);
		dhcpStateContent= (TextView) findViewById(R.id.dhcpstatecontent);	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ethernetdhcpinfo_setting);
		ethernetIP.transContext(this);
		findView();
		//mIntentFilter = new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
		mEthMgr = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("EthernetDHCPInfoSetting",Thread.currentThread().getStackTrace()[2].getMethodName());
                ethernetIP.switchEthernetMode(EthernetIP.ETHER_DHCP); //鍒囨崲鍒板姩鎬両P

		this.registerReceiver(mReceiver, mIntentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}

	public void netDialog(String info) {
		Log.i("EthernetDHCPInfoSetting",Thread.currentThread().getStackTrace()[2].getMethodName());
		new AlertDialog.Builder(this).setMessage(info)
				.setPositiveButton("纭畾", null).show();

	}

}
