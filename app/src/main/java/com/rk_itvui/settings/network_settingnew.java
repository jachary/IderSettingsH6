package com.rk_itvui.settings;

import java.util.ArrayList;
import java.util.HashMap;
import com.rk_itvui.settings.FullScreenActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.rk_itvui.settings.network.WifiApFragment;
import com.rk_itvui.settings.network.WifiFragment;
import com.rk_itvui.settings.network.WireNetworkFragment;
import com.rk_itvui.settings.network.WireNetworkSetting;
import com.rk_itvui.settings.network.WirelessNetworkFragment;
import com.rk_itvui.settings.network.wifi.WifiAp_Settings;
import com.rk_itvui.settings.network.wifi.Wifi_setting;
import com.rk_itvui.settings.pppoe.PppoeDialog;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;
import com.zxy.idersettings.R;
public class network_settingnew extends FullScreenActivity implements OnItemClickListener {
	/** Called when the activity is first created. */
	
	ListView list;
	SimpleAdapter listItemAdapter;
	// 鐢熸垚鍔ㄦ�佹暟缁勶紝鍔犲叆鏁版嵁
	ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
	// 鐢熸垚涓や釜HashMap绫诲瀷鐨勫彉閲弇ap1 锛� map2
	// HashMpa涓洪敭鍊煎绫诲瀷銆傜涓�涓弬鏁颁负閿紝绗簩涓弬鏁颁负鍊�
	HashMap<String, Object> map_WiredItem = new HashMap<String, Object>();
	HashMap<String, Object> map_WiFiItem = new HashMap<String, Object>();
	HashMap<String, Object> map_WirelessStatusItem = new HashMap<String, Object>();
	HashMap<String, Object> map_WiFiApItem = new HashMap<String, Object>();
	HashMap<String, Object> map_Dialing = new HashMap<String, Object>();
	/*鍥涗釜fragment澹版槑*/
	WireNetworkFragment wireNetworkFragment;
	WifiFragment wifiFragment;
	//WirelessNetworkFragment wirelessNetworkFragment;
	WifiApFragment wifiApFragment;
	
	public void initFragment(){
		wireNetworkFragment = new WireNetworkFragment(wireNetworkHandler,this);
		wifiFragment = new WifiFragment(wifiHandler,this);
	//	wirelessNetworkFragment= new WirelessNetworkFragment();
		wifiApFragment = new WifiApFragment(wifiApHandler,this);
		
		getFragmentManager().beginTransaction().replace(R.id.networkInfo, wifiApFragment).commit();
		//getFragmentManager().beginTransaction().replace(R.id.networkInfo, wirelessNetworkFragment).commit();
		getFragmentManager().beginTransaction().replace(R.id.networkInfo, wifiFragment).commit();
		getFragmentManager().beginTransaction().replace(R.id.networkInfo, wireNetworkFragment).commit();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_settingnew);
		addListView();
		initFragment();
		
		/*榛樿閫夋嫨绗竴椤�*/
		getFragmentManager().beginTransaction()
				.replace(R.id.networkInfo, wireNetworkFragment).commit();
		registerReciver(); //闇�瑕佷负鍚凢ragment 娉ㄥ唽骞挎挱
			ActivityAnimationTool.prepareAnimation(this);
			ActivityAnimationTool.animate(this, 1000);
	}


	public void addListView() {
		// 鎶婃暟鎹～鍏呭埌map涓��
		String wireNetwork=this.getResources().getString(R.string.ethernet);
		String wireNetworkInfo=this.getResources().getString(R.string.disconncect);
                
		map_WiredItem.put("networkSettingIcon", R.drawable.network_icon_eth);
		map_WiredItem.put("networkSettingItem", wireNetwork);
		map_WiredItem.put("networkSettingStatus", wireNetworkInfo);

		String wifiInfo=this.getResources().getString(R.string.off);
		map_WiFiItem.put("networkSettingIcon", R.drawable.network_icon_wifi);   //涓嶇炕璇�
		map_WiFiItem.put("networkSettingItem", "Wi-Fi");   //涓嶇炕璇�
		map_WiFiItem.put("networkSettingStatus", wifiInfo);

	/*	map_WirelessStatusItem.put("networkSettingItem", "鏃犵嚎缃戠粶");
		map_WirelessStatusItem.put("networkSettingStatus", "鏈繛鎺�");*/
		String Ap=this.getResources().getString(R.string.Ap);
		String apInfo=this.getResources().getString(R.string.accessibility_service_state_off);
		
		map_WiFiApItem.put("networkSettingIcon", R.drawable.network_icon_hotspot);
		map_WiFiApItem.put("networkSettingItem", Ap);
		map_WiFiApItem.put("networkSettingStatus", apInfo);

//		map_Dialing.put("networkSettingIcon", R.drawable.network_icon_eth);
//		map_Dialing.put("networkSettingItem", getString(R.string.pppoe_settings));
		
		// 鎶妋ap1鍜宮ap2娣诲姞鍒發ist涓�
		listItem.add(map_WiredItem);
		listItem.add(map_WiFiItem);
	//	listItem.add(map_WirelessStatusItem);
		listItem.add(map_WiFiApItem);
		//listItem.add(map_Dialing);
		list = (ListView) findViewById(R.id.networkListView);

	        listItemAdapter = new SimpleAdapter(this,
				listItem,// 鏁版嵁婧�
				R.layout.network_item,// ListItem鐨刋ML瀹炵幇
				// 鍔ㄦ�佹暟缁勪笌ImageItem瀵瑰簲鐨勫瓙椤�
				new String[] { "networkSettingIcon","networkSettingItem", "networkSettingStatus" },
				new int[] { R.id.networkSettingIcon,R.id.networkSettingItem, R.id.networkSettingStatus });

		// 娣诲姞骞朵笖鏄剧ず
		list.setAdapter(listItemAdapter);
		// 娣诲姞鐐瑰嚮浜嬩欢
		list.setOnItemClickListener(this);
		// 娣诲姞閬ユ帶鍣ㄧ殑閫変腑锛堣幏鍙栫劍鐐瑰悗鐨勪簨浠讹級
		list.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (0 == arg2) {			
					getFragmentManager().beginTransaction()
							.replace(R.id.networkInfo, wireNetworkFragment).commit();
					//fragment.getHandlerMethod(mHandler);
				}
				if (1 == arg2) {
					getFragmentManager().beginTransaction()
							.replace(R.id.networkInfo, wifiFragment).commit();
				}
				if (2 == arg2) {				
					getFragmentManager().beginTransaction().replace(R.id.networkInfo, wifiApFragment).commit();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	public void startIntentWithTranlete(Activity act,Intent intent){
		ActivityAnimationTool.startActivity(act, intent);
	}
	/**
	 * 澶勭悊ListView鐨勭偣鍑讳簨浠�
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
		switch (index) {
		case 0:

			Intent intent_wireNetwork = new Intent(this, WireNetworkSetting.class);
			startIntentWithTranlete(network_settingnew.this,intent_wireNetwork);
			break;
		case 1:
			
			Intent intent_wifi = new Intent(this, Wifi_setting.class);
			startIntentWithTranlete(network_settingnew.this,intent_wifi);
			break;
		case 2:
			Intent intent_wifiApNetwork = new Intent(this, WifiAp_Settings.class);
			startIntentWithTranlete(network_settingnew.this,intent_wifiApNetwork);
			break;
		case 3:
			Intent intent_Dialing = new Intent(this,PppoeDialog.class);
			startIntentWithTranlete(network_settingnew.this, intent_Dialing);
		}
	}
	
	private Handler wireNetworkHandler=new Handler() {
		
		public void handleMessage(Message msg) {
			String netStateInfo;

			switch(msg.what){
			case 0:  //鏈夌嚎缃戠粶鏂紑
				netStateInfo=getResources().getString(R.string.wiredNetworkUnconnected);
				map_WiredItem.put("networkSettingStatus", netStateInfo);
				break;
			case 1://鏈夌嚎缃戠粶杩炴帴
				netStateInfo=getResources().getString(R.string.wiredNetworkConnected);
				map_WiredItem.put("networkSettingStatus", netStateInfo);	
				break;
			}
			listItemAdapter.notifyDataSetChanged();
		}
		
	};
	private Handler wifiHandler = new Handler() {
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
			
		    netStateInfo=getResources().getString(hint);
			map_WiFiItem.put("networkSettingStatus", netStateInfo);	
			listItemAdapter.notifyDataSetChanged();
		}
	};
	private Handler wifiApHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d("blb","wifiApHandler ,handleMessage() msg.what = " + msg.what);
			String netStateInfo;
			int hint;
			switch (msg.what) {
			case WifiManager.WIFI_AP_STATE_ENABLING:
				hint=R.string.wifi_starting;
				break;
			case WifiManager.WIFI_AP_STATE_ENABLED:

				hint=R.string.accessibility_service_state_on;

				break;
			case WifiManager.WIFI_AP_STATE_DISABLING:
			
				hint=R.string.wifi_stopping;
				break;
			case WifiManager.WIFI_AP_STATE_DISABLED:
			   hint=R.string.accessibility_service_state_off;
				break;
			default:
				   hint=R.string.wifi_error;
			}
	    	netStateInfo=getResources().getString(hint);
			map_WiFiApItem.put("networkSettingStatus", netStateInfo);	
			listItemAdapter.notifyDataSetChanged();
		}
	};
	@Override
	protected void onResume(){  
		super.onResume();
		wireNetworkFragment.checkNetMode();
		/*
		wifiApFragment.resume();	
		wifiFragment.resume();
		wireNetworkFragment.resume();*/
	}
	private void registerReciver(){
		/*
		wifiApFragment.resume();	
    	wifiFragment.resume();
		wireNetworkFragment.resume();*/
	}
	@Override
	protected void onDestroy(){ 
		super.onDestroy();
		wireNetworkFragment.pause();
		wifiFragment.pause();
		wifiApFragment.pause();	
	}
	
	@Override
	protected void onPause(){  
		super.onPause();
		/*
		wireNetworkFragment.pause();
		wifiFragment.pause();
		wifiApFragment.pause();		*/
	}

}
