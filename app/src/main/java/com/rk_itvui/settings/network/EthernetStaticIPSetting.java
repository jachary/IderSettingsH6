package com.rk_itvui.settings.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.FullScreenActivity;
public class EthernetStaticIPSetting extends FullScreenActivity {

	public EditText ipAddr;
	public EditText netMask;
	public EditText gateWay;
	public EditText dns1;
	public EditText dns2;

	private static final int KEY_USE_STATIC_IP = R.id.ipAddrValue; //
	private static final int KEY_IP_ADDRESS = R.id.ipAddrValue; // 鍚屼笂
	private static final int KEY_GATEWAY = R.id.gateWayValue; // 鍚屼笂
	private static final int KEY_NETMASK = R.id.netMaskValue; // 鍚屼笂
	private static final int KEY_DNS1 = R.id.dns1Value; // 鍚屼笂
	private static final int KEY_DNS2 = R.id.dns2Value; // 鍚屼笂

	/** 鍚� static IP 璁剧疆 鐩稿叧鐨� Preference 瀹炰緥鐨� key 瀛椾覆鏁扮粍. */
	private int[] mTexteditKeys = { KEY_IP_ADDRESS, KEY_GATEWAY, KEY_NETMASK,
			KEY_DNS1, KEY_DNS2, };

	EthernetIP ethernetIP = new EthernetIP();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ethernetstaticip_setting);
		ethernetIP.transContext(this);
		updateIpSettingsInfo();
	}

	/*
	 * 鏇存柊UI
	 */
	public void updateIpSettingsInfo() {
		ipAddr = (EditText) findViewById(R.id.ipAddrValue);
		netMask = (EditText) findViewById(R.id.netMaskValue);
		gateWay = (EditText) findViewById(R.id.gateWayValue);
		dns1 = (EditText) findViewById(R.id.dns1Value);
		dns2 = (EditText) findViewById(R.id.dns2Value);

                char staticIP=0;
		ipAddr.setText(TextUtils.isEmpty(ethernetIP.getIPAddress(staticIP))?
                                EthernetIP.defaultIPAdress:
                                      ethernetIP.getIPAddress(staticIP));
		netMask.setText(TextUtils.isEmpty(ethernetIP.getNetMask(staticIP))?
                                EthernetIP.defaultIPNetMask:
                                      ethernetIP.getNetMask(staticIP));
		gateWay.setText(TextUtils.isEmpty(ethernetIP.getGateWay(staticIP))?
                                EthernetIP.defaultGateWay:
                                      ethernetIP.getGateWay(staticIP));
		dns1.setText(TextUtils.isEmpty(ethernetIP.getDNS1(staticIP))?
                                EthernetIP.defaultDNS1:
                                      ethernetIP.getDNS1(staticIP));
		dns2.setText(TextUtils.isEmpty(ethernetIP.getDNS2(staticIP))?
                                EthernetIP.defaultDNS2:
                                      ethernetIP.getDNS2(staticIP));
	}

	/*
	 * 鎵�鏈変笉涓虹┖锛屼笉姝ｇ‘IP鍦板潃灏嗕笉鑳藉姝ｇ‘淇濆瓨锛圗thernetIP绫讳細鍋氶獙璇侊級
	 */
	public void onButtonSave(View view) {
		EditText edit;
		boolean changOK = true;
		if (isIpDataInUiComplete()) { // IP鍦板潃锛屽瓙缃戞帺鐮侊紝缃戝叧锛屼富DNS涓嶄负绌�
			ipAddr = (EditText) findViewById(R.id.ipAddrValue);
			netMask = (EditText) findViewById(R.id.netMaskValue);
			gateWay = (EditText) findViewById(R.id.gateWayValue);
			dns1 = (EditText) findViewById(R.id.dns1Value);
			dns2 = (EditText) findViewById(R.id.dns2Value);

			String ipText = ipAddr.getText().toString();
			String netMaskText = netMask.getText().toString();
			String gateWayText = gateWay.getText().toString();
			String dns1Text = dns1.getText().toString();
			String dns2Text = dns2.getText().toString();
			if (!ethernetIP.setIPAddress(ipText)) {
				changOK = false;
				netDialog("IP鍦板潃涓嶆纭�");
			}
			if (!ethernetIP.setNetMask(netMaskText)) {
				changOK = false;
				netDialog("瀛愮綉鎺╃爜涓嶆纭�");
			}
			if (!ethernetIP.setGateWay(gateWayText)) {
				changOK = false;
				netDialog("缃戝叧涓嶆纭�");
			}
			if (!ethernetIP.setDNS1(dns1Text)) {
				changOK = false;
				netDialog("DNS1 is not right");

			}
			if (!dns2Text.isEmpty()) // DNS2鐢变簬涓嶆槸蹇呭～椤癸紝鎵�浠ワ紝闇�瑕佸厛楠岃瘉鍏舵槸濉啓
			{
				if (!ethernetIP.setDNS2(dns2Text)) {
					changOK = false;
					netDialog("DNS is not right");
				}
			}
			if(changOK)
			{

                        /*
				//濡傛灉pppoe 杩炴帴锛岄渶瑕佸厛鏂紑
				PppoeManager mPppoeMgr= (PppoeManager) getSystemService(Context.PPPOE_SERVICE);
				int mPppoeState;
					mPppoeState = mPppoeMgr.getPppoeState();
				if (mPppoeState == PppoeManager.PPPOE_STATE_CONNECTED
						|| mPppoeState == PppoeManager.PPPOE_STATE_CONNECTING) { 
					mPppoeMgr.stopPppoe();
				} 
                         */
			//	ethernetIP.enableEthernetStaticIP(); //濡傛灉淇敼鎴愬姛锛岃繖閲岄渶瑕侀噸鍚互澶綉
                ethernetIP.switchEthernetMode(EthernetIP.ETHER_STATIC);
				netDialog("OK");
			}
		}else{
				netDialog("濉啓涓嶅畬鏁�");
		}
	}

	public void netDialog(String info) {
		new AlertDialog.Builder(this).setMessage(info)
				.setPositiveButton("纭畾", null).show();

	}

	public void onButtonCancel(View view) {
		updateIpSettingsInfo();
		finish();
	}

	private boolean isIpDataInUiComplete() {
		/* 閬嶅巻鍑篸ns2澶栫殑鎵�鏈夎緭鍏ユ, ... */
		for (int i = 0; i < (mTexteditKeys.length - 1); i++) {
			EditText edit = (EditText) findViewById(mTexteditKeys[i]);
			String text = edit.getText().toString();
			Log.d("isIpDataInUiComplete() ", " text = " + text);
			/* 鑻ュ綋鍓� IP 鍙傛暟 涓� null 鎴栬�� 涓� 绌哄瓧涓�, 鍒� ... */
			if (null == text || TextUtils.isEmpty(text)) {
				/* 杩斿洖鍚﹀畾缁撴灉. */
				return false;
			}
		}
		/* 杩斿洖鑲畾. */
		return true;
	}
}
