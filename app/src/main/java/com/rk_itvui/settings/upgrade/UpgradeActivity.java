/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014      fxw         1.0         create
*******************************************************************/   

package com.rk_itvui.settings.upgrade;

import java.util.ArrayList;

import com.rk_itvui.settings.Settings;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.adapter.SettingListAdapter;
import com.rk_itvui.settings.model.ListItem;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;
import com.rk_itvui.utils.ReflectionUtils;
import com.rk_itvui.utils.WindowHelper;
import com.rk_itvui.settings.FullScreenActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpgradeActivity extends FullScreenActivity implements View.OnClickListener {
	private LinearLayout mupdateider;
	private LinearLayout mupdateapp;
	private static final int STATE_DETECT = 1;
	private static final int STATE_UPDATE = 2;
	private int mState;
	private Button mUpgradeButton;
	private TextView mUpgradeTxt;
	private ProgressBar mUpgradeProgress;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting_upgrade);
		ListView deviceListView = (ListView)findViewById(R.id.list_view);
		mUpgradeButton = (Button)findViewById(R.id.upgrade_btn_upgrade);
		mUpgradeTxt = (TextView)findViewById(R.id.upgrade_txt_update);
		mUpgradeProgress = (ProgressBar)findViewById(R.id.upgrade_progress);
		mupdateapp = (LinearLayout) findViewById(R.id.updateapp);
		mupdateider = (LinearLayout) findViewById(R.id.updateider);
		mupdateapp.setOnClickListener(this);
		mupdateider.setOnClickListener(this);
		mUpgradeButton.setOnClickListener(this);
		ArrayList<ListItem> itemList = buildListItem();
		SettingListAdapter upgradeAdapter = new SettingListAdapter(this, itemList);
		deviceListView.setAdapter(upgradeAdapter);
		
		ViewGroup.LayoutParams  progresslp = mUpgradeProgress.getLayoutParams();
		progresslp.width = (int)(0.7*WindowHelper.getWinWidth(this));
		mUpgradeProgress.setLayoutParams(progresslp);
		
		mState = STATE_DETECT;
		ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
	}
	
	private ArrayList<ListItem> buildListItem(){
		ArrayList<ListItem> itemList = new ArrayList<ListItem>();
		ListItem item = new ListItem();
//		item.setIconRes(R.drawable.upgrade_device);
//		item.setTitle(getString(R.string.upgrade_device_name));
//		item.setDetail(Build.MODEL);
//		itemList.add(item);
//		
//		//wifi mac
//		item = new ListItem();
//		item.setIconRes(R.drawable.upgrade_wifi);
//		item.setTitle(getString(R.string.upgrade_wifi_mac));
//		item.setDetail(getWifiMac());
//		itemList.add(item);
//		//ethernet mac
//		item = new ListItem();
//		item.setIconRes(R.drawable.upgrade_ethernet);
//		item.setTitle(getString(R.string.upgrade_ether_mac));
//		item.setDetail(getEthernetMac());
//		itemList.add(item);
		//version
//		item = new ListItem();
//		item.setIconRes(R.drawable.upgrade_version);
//		item.setTitle(getString(R.string.upgrade_version));
//		item.setDetail(getProductVersion());
//		itemList.add(item);
//		
//		item = new ListItem();
//		item.setIconRes(R.drawable.upgrade_version);
//		item.setTitle(getString(R.string.update_version));
//		item.setDetail(getProductVersion());
//		itemList.add(item);
		return itemList;
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.upgrade_btn_upgrade){
			if(mState==STATE_DETECT){
				detectVersion();	
			}else if(mState==STATE_UPDATE){
				upgradeVersion();
			}
		}
			switch (v.getId()) {
			case R.id.updateapp:
		        Intent intent = getPackageManager().getLaunchIntentForPackage("com.yunos.tv.osupdate");
	            if(intent!=null){
	                startActivity(intent);
	            }
				break;
			case R.id.updateider:
				  Intent intent1 = getPackageManager().getLaunchIntentForPackage("com.ider.update");
		            if(intent1!=null){
		                startActivity(intent1);
		            }
				break;

			default:
				break;
			}
		}
	
	
	/**
	 */
	public void detectVersion(){
		int COMMAND_CHECK_REMOTE_UPDATING_BY_HAND = 3;
		Intent serviceIntent = new Intent("android.rockchip.update.service");
        serviceIntent.putExtra("command", COMMAND_CHECK_REMOTE_UPDATING_BY_HAND);
        startService(serviceIntent);
		//mUpgradeTxt.setText(R.string.upgrade_version_new);
	}
	
	/**
	 */
	public void upgradeVersion(){
		//TODO
	}
	
	/**
	 * @return
	 */
	private String getWifiMac(){
		WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if(wifiInfo!=null) return wifiInfo.getMacAddress();
		return null;
	}
	
	/**
	 * @return
	 */
	private String getEthernetMac(){
		Object ethManager = getSystemService("ethernet");
		Object ifaceName = ReflectionUtils.invokeMethod(ethManager, "getEthernetIfaceName");
		String ethMac = (String)ReflectionUtils.invokeMethod(ethManager, "getEthernetHwaddr", ifaceName);
		return ethMac;
	}
	
	/**
	 */
	private String getProductVersion(){
		String version = (String)ReflectionUtils.invokeStaticMethod("android.os.SystemProperties", "get", "ro.product.version", "");
		return version;
	}

}
