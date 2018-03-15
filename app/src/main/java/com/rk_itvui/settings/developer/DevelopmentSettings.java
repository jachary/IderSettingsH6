/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     huangjc@rock-chips.com
* Create at:   2014骞�7鏈�9鏃� 涓嬪崍4:42:08  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014骞�7鏈�9鏃�      huangjc         1.0         create
*******************************************************************/

package com.rk_itvui.settings.developer;

import com.zxy.idersettings.R;
import com.rk_itvui.settings.FullScreenActivity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.app.Activity;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.AlertDialog;
import android.widget.EditText;
import android.view.View;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TabHost;
import android.content.res.Resources;
import android.view.LayoutInflater;
import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.Map;
import java.util.HashMap;
import android.widget.Toast;
import android.widget.TabWidget;
import android.widget.AdapterView;
import android.os.AsyncTask;

import android.content.Intent;
import android.graphics.Color;
import android.bluetooth.BluetoothAdapter;
import android.view.Window;
import android.os.SystemClock;
import android.content.ComponentName;
import java.io.File; 

import com.rk_itvui.settings.deviceinfo.UsbMode;
import com.rk_itvui.settings.dialog.UnknownSources;
import com.rk_itvui.settings.dialog.EventNotificationSetting;
import com.rk_itvui.settings.dialog.UsbDebugging;
import com.rk_itvui.settings.dialog.UsbModeSettings;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;
import com.rk_itvui.settings.ScreenInformation;

import android.graphics.drawable.Drawable;
import android.widget.Button;

import android.util.DisplayMetrics;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;



public class DevelopmentSettings extends FullScreenActivity
{
	private String TAG = "DevelopmentSettings";
	private Bundle mBundle = null;
	RelativeLayout mLayout = null;
	private TextView mTextView = null;
	private ImageView mImageView = null;
	private TabHost mTabHost = null;
	private ListView mListView = null;
	private UsbMode mUsbMode = null;
	private SettingItemAddManager mSettingItemManager = null; 
		
	//鐢ㄤ簬淇濆瓨development涓嬬殑瀛楃璧勬簮鐨処D,璇D鐢ㄦ潵鎻愬彇鍑哄瓧绗﹁祫婧愶紝骞跺皢璇D鍙疯缃负瀵瑰簲View鐨則ag锛屾牴鎹甐iew鐨則ag鏉ュ垽鏂�
	// 鍝釜view琚偣鍑伙拷
	private Map<String, ArrayList<SettingItem>> mMap = new HashMap<String, ArrayList<SettingItem>>();
	//  ListView鐨凙dapter
	private ListViewAdapter mListViewAdapter = null;

	/*usb mode setting*/
	UsbModeSettings mUsbModeSetting = null;
	/*event notification setting*/
	EventNotificationSetting mEventNotification = null;
	/*permit unknown sources*/
	UnknownSources mUnknownSources = null;
	/*usb debugging*/
	UsbDebugging mUsbDebugging = null;

	@SuppressLint("NewApi")
	private void getScreenSize()	
	{		
		DisplayMetrics displayMetrics = new DisplayMetrics();	        
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);	        
		ScreenInformation.mScreenWidth = displayMetrics.widthPixels;	        
		ScreenInformation.mScreenHeight = displayMetrics.heightPixels;		 
		ScreenInformation.mDensityDpi = displayMetrics.densityDpi;		 
		ScreenInformation.mDpiRatio = ((float)ScreenInformation.mDefaultDpi)/(float)displayMetrics.densityDpi;	        
	}

	private Bitmap bitMapScale(int id,float scaleParameter)
	{
		Bitmap map = BitmapFactory.decodeResource(this.getResources(),id);
		float scale = ScreenInformation.mScreenWidth/1280f*scaleParameter;
		int width = (int)((float)map.getWidth()*scale);
		int height = (int)((float)map.getHeight()*scale);

 		Bitmap resize = Bitmap.createScaledBitmap(map, width, height, true);
		return resize;
	}

	private void createSpace()
	{
//		TextView headSpace = (TextView)findViewById(R.id.head_space);
		TextView buttom = (TextView)findViewById(R.id.bottom_space);
		int height = (int)(ScreenInformation.mScreenWidth/20f);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,height);
//		headSpace.setLayoutParams(params);
		buttom.setLayoutParams(params);
		ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBundle = savedInstanceState;
		int level = 0;
		int indicator = 0;
		int parent = -1;
		setContentView(R.layout.developmentsetting);
		getScreenSize();
		createSpace();
		if(mListView == null)
			createListView();
		if(savedInstanceState != null)
		{
			level = savedInstanceState.getInt("Level");
			indicator = savedInstanceState.getInt("Indicator");
			parent = savedInstanceState.getInt("Parent");
			LOGD("onCreate(), level = "+level+", indicator = "+indicator+",parentId = "+parent);
		}
		setTabHostFocus(indicator,level,parent);
		initContentStatus();
		mSettingItemManager = SettingItemAddManager.getInstance();
		mSettingItemManager.setContentMap(mMap);

	}
	// 璁剧疆褰撳墠TabHost鐨勯�変腑椤�
		private void setTabHostFocus(int current,int level, int parent) {
			mListViewAdapter.setLevel(level);
			mListViewAdapter.setParentId(parent);
			mListViewAdapter.setSelection(current);
			mListViewAdapter.invalidate();
			mListView.requestFocus();
			mListView.setSelection(0);
			mListView.invalidate();
		}
	private void getEventNotificationDefault() {
		mEventNotification = new EventNotificationSetting(DevelopmentSettings.this,
				mHandler, mListViewAdapter);
	}

	private void getUsbModeSettingDefault() {
		mUsbModeSetting = new UsbModeSettings(DevelopmentSettings.this, mHandler, mListViewAdapter);
	}

	private void getUnknownSourcesDefault() {
		mUnknownSources = new UnknownSources(DevelopmentSettings.this, mHandler,
				mListViewAdapter);
	}

	private void getUsbDebuggingDefault() {
		mUsbDebugging = new UsbDebugging(DevelopmentSettings.this, mHandler,
				mListViewAdapter);
	}

	//鍒濆鍖栨瘡涓�椤圭殑鐘舵�佸�硷紝姣忎竴椤归渶瑕佹樉绀虹姸鎬佺殑椤瑰湪杩欓噷璁剧疆鐘舵��
	// 鐘舵�佸�间竴鑸粠鏁版嵁搴撲腑鑾峰彇鎴栬�呮牴鎹‖浠剁殑鍒濆鍖栨垚鍔熷け璐ユ儏鍐垫潵璁剧疆
	@SuppressLint("NewApi")
	private void initContentStatus() {
		Log.d(TAG, "OnCreate:initContentStatus()==================");
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... unused) {
				loadSystemResource();

				// add code here
				getUsbModeSettingDefault();
		//		getEventNotificationDefault();
				getUnknownSourcesDefault();
				getUsbDebuggingDefault();
	       			//UsbModeSettingsInit();
				//createListView();
				mHandler.sendEmptyMessageDelayed(
						SettingMacroDefine.upDateListView, 10);
				return null;
			}
		}.execute();
	}
	
	private void UsbModeSettingsInit(){
		mUsbMode = new UsbMode(this, mHandler);
		mUsbMode.Resume();
	}
	
	/*
	 * end
	 */
	// create ListView bind to view
	private View createListView() {
		mListView = (ListView) findViewById(R.id.tabconent_list);
		mListViewAdapter = new ListViewAdapter(this, mMap);
		mListView.setAdapter(mListViewAdapter);
		mListView.setOnItemClickListener(mListItemClister);
		mListView.setOnItemLongClickListener(mLongListItemClickListener);
		return mListView;
	}
	private TabHost.TabContentFactory mContentFactory = new TabHost.TabContentFactory() {
		public View createTabContent(String tag) {
			if(mListView == null)
				return createListView();
			else
				return mListView;
		}
	};
	// 鍔犺浇璧勬簮
	private void loadSystemResource() {
		// development
		SettingItem[] resources = {
				// USB
				new SettingItem(0, -1, R.string.usb_setting),
				// Event Notification
		//		new SettingItem(0, -1, R.string.event_notification),
				// Unknown Sources
				new SettingItem(0, -1, R.string.unknown_sources),
				// Usb Debugging
				new SettingItem(0, -1, R.string.usb_debugging),
			//	new SettingItem(0, -1, R.string.storage_menu_usb),
				new SettingItem(0, -1, R.string.more_settings,null,null,BitmapFactory.decodeResource(this.getResources(),R.drawable.advance))
                
		};
//		Log.d(TAG,"mMap="+mMap);
		if (mMap != null) {
			ArrayList<SettingItem> array = new ArrayList<SettingItem>();
			for (int i = 0; i < resources.length; i++) {
				array.add(resources[i]);
			}
			mMap.put("development", array);
		}
	}

	private void showToast(String message){
		Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
	}
	

	private AdapterView.OnItemClickListener mListItemClister = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			LOGD("view's tag = " + view.getTag() + ",position = " + position
					+ ",arg3 = " + arg3);
			int id = ((Integer) view.getTag()).intValue();
			if (!haveChild(mListViewAdapter.getContent(),id)) {
				if(!settingItemClick(id))
				{
					onSystemItemClick(view, position);
				}
			}
		}
	};

	private AdapterView.OnItemLongClickListener mLongListItemClickListener = new AdapterView.OnItemLongClickListener()
	{
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3)
		{
			int id = ((Integer) view.getTag()).intValue();
			if (!haveChild(mListViewAdapter.getContent(),id)) 
			{
				if(!settingItemLongClick(id))
				{
					// add code here
				}
			}


			return true;
		}
	};
	
	private boolean settingItemClick(int id)
	{
		SettingItem item = null;
		if(((item = findSettingItem("development",id)) != null))
		{
			return item.onSettingItemClick(id);
		}

		return false;
	}


	private boolean settingItemLongClick(int id)
	{
		SettingItem item = null;
		if(((item = findSettingItem("development",id)) != null))
		{
			return item.onSettingItemLongClick(id);
		}

		return false;
	}
	
	private boolean haveChild(String name, int id) {
		LOGD("havaChild(),name = " + name + ",id = " + id);
		if ((mMap != null) && (name != null)) {
			ArrayList array = (ArrayList<SettingItem>) mMap.get(name);
			if (array != null) {
				for (int i = 0; i < array.size(); i++) {
					SettingItem item = (SettingItem) array.get(i);
					if (item.mParentId == id) {
							mListViewAdapter.setParentId(id);
							mListViewAdapter.setLevel(item.mLevel);
							mListViewAdapter.invalidate();
						return true;
					}
				}
			}
		}

		return false;
	}

	private SettingItem findParent(String name, int Id) {
		if ((mMap != null) && (name != null)) {
			ArrayList array = (ArrayList<SettingItem>) mMap.get(name);
			if (array != null) {
				for (int i = 0; i < array.size(); i++) {
					SettingItem item = (SettingItem) array.get(i);
					if (item.mId == Id) {
						return item;
					}
				}
			}
		}

		return null;
	}

	// 鍒楄〃鍗曞嚮浜嬩欢澶勭悊
	private void onSystemItemClick(View view, int position) {
		if (view == null)
			return;

		int tag = ((Integer) view.getTag()).intValue();
		switch (tag) {
		// add code here
		case R.string.usb_setting:
			mUsbModeSetting.onUsbModeClick();
			break;
//		case R.string.event_notification:
//			mEventNotification.onEventNotificationClick();
//			break;
//       fix by huangjc for huawei3066
         	case R.string.unknown_sources:
			mUnknownSources.SourcesUnknown();
			break;
		case R.string.usb_debugging:
			mUsbDebugging.DebuggingUsb();
			break;	
	/*	case R.string.storage_menu_usb:
			StartActivityForResultSafely(new Intent(this, com.rk_itvui.settings.deviceinfo.UsbSettings.class),
					UsbMode.REQUEST_CODE_USB_CONNECT_MODE);
                        break;*/
                case R.string.more_settings:
		        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClassName("com.android.settings","com.android.settings.Settings");
                        startActivity(intent);
                        break;
                default:break;
		}
	}

	private void LOGD(String msg) {
		if (true)
			Log.d(TAG, msg);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			LOGD("mHandler,handleMessage() msg.what = " + msg.what);
			switch (msg.what) {
			case SettingMacroDefine.upDateListView:
				if (mListViewAdapter != null) {
					mListViewAdapter.invalidate();
				}
				break;
			case SettingMacroDefine.setSettingItemClickable:
				{
					int id = (int)msg.arg1;
					boolean canclick = ((int)msg.arg2 == 1);
					setSettingItemClickable(id,canclick);

					if (mListViewAdapter != null) 
					{
						mListViewAdapter.invalidate();
					}
					break;
				}
			case SettingMacroDefine.setSettingItemView:
				{
					int id = (int)msg.arg1;
					View view = (View)msg.obj;
					setSettingItemView(id,view);

					if (mListViewAdapter != null) 
					{
						mListViewAdapter.invalidate();
					}
				}
				break;
			case SettingMacroDefine.setSettingItemFucntion:
				setSettingItemCallBackFunction(msg.arg1,(SettingItemClick)msg.obj);
				break;
			}
		}
	};

	public SettingItem findSettingItem(String content,int id)
	{
		if(mMap == null)
			return null;
		
		ArrayList<SettingItem> list = (ArrayList<SettingItem>)mMap.get(content);
		if(list != null)
		{
			for(int i = 0; i < list.size(); i++)
			{
				SettingItem item = list.get(i);
				if(item.mId == id)
				{
					return item;
				}
			}
		}

		return null;
	}

	public void updateSettingItem(int id,String status, String summary, Bitmap draw)
	{
		SettingItem item = null;
		if(((item = findSettingItem("development",id)) != null))
		{
			if(status != null)
				item.setStatus(status);
			if(summary != null)
				item.setSummary(summary);
			if(draw != null)
				item.setDrawable(draw);
		}
	}

	public void  updateSettingItem(int id,int statusId, int summaryId,int bitmapId)
	{
		String status = null;
		String summary = null;
		Bitmap drawable = null;
		if(statusId != -1)
			status = this.getResources().getString(statusId);
		if(summaryId != -1)
			summary = this.getResources().getString(summaryId);
		if(bitmapId != -1)
			drawable = BitmapFactory.decodeResource(this.getResources(),bitmapId);//this.getResources().getDrawable(bitmapId);

		updateSettingItem(id,status,summary,drawable);
	}

	public void setSettingItemView(int id,View view)
	{
		if(mMap != null)
		{
			SettingItem item = null;
			if(((item = findSettingItem("development",id)) != null))
			{
				item.setView(view);
			}
			
		}
	}
	
	public boolean setSettingItemClickable(int id,boolean click)
	{
		if(mMap != null)
		{
			SettingItem item = null;
			if(((item = findSettingItem("development",id)) != null))
			{
				item.setClickable(click);
				return true;
			}
			
		}
		return false;
	}

	

	public void setSettingItemCallBackFunction(int id, SettingItemClick function)
	{
		if(mMap != null)
		{
			SettingItem item = null;
			if(((item = findSettingItem("development",id)) != null))
			{
				LOGD("setSettingItemCallBackFunction(), setFunction, id = "+id);
				item.setOnSettingItemClick(function);
			}
			
		}
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();

		LOGD("dispatchKeyEvent(),keyCode = " + keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				int level = mListViewAdapter.getLevel();
				if (level > 0) {
					level--;
					mListViewAdapter.setLevel(level);
					String name = mListViewAdapter.getContent();
					int parent = mListViewAdapter.getParentId();
					SettingItem item = findParent(name, parent);
					LOGD("dispatchKey,Key_Back, name = " + name
							+ ",parent id = " + parent);
					if (item == null) {
						mListViewAdapter.setParentId(-1);
						// mListViewAdapter.setLevel(0);
					} else {
						mListViewAdapter.setParentId(item.mParentId);
					}
					mListViewAdapter.invalidate();

					return true;
				}
			}
			break;
		}

		return super.dispatchKeyEvent(event);
	}

	public void onSaveInstanceState(Bundle savedState) 
	{
		if(mListViewAdapter != null)
		{
			LOGD("onSaveInstanceState(),Level = "+mListViewAdapter.getLevel()+", Indicator = "+mListViewAdapter.getSelection()
				+",ParentId = "+mListViewAdapter.getParentId());
			savedState.putInt("Level", mListViewAdapter.getLevel());
	        savedState.putInt("Indicator", mListViewAdapter.getSelection());
			savedState.putInt("Parent", mListViewAdapter.getParentId());
		}
	}
	public void onResume() {
		Log.d(TAG, "OnResume:==================");
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}


	public void onDestroy()
	{
		super.onDestroy();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		LOGD("onActivityResult requestcode:"+requestCode);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {			
		case UsbMode.REQUEST_CODE_USB_CONNECT_MODE:
			String usbmode = data.getStringExtra(UsbMode.USB_MODE);
			LOGD("usbmode:"+usbmode);
			if (mUsbMode != null){
				mUsbMode.updateMode(usbmode);
			}
			break;

		default:
			break;
		}
		
	}
	
	public void StartActivityForResultSafely(Intent intent , int requestCode){
		try {
			startActivityForResult(intent, requestCode);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Unable to launch intent=" + intent, e);
		} catch (SecurityException e) {
			Toast.makeText(this, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG,
							"Launcher does not have the permission to launch "
									+ intent
									+ ". Make sure to create a MAIN intent-filter for the corresponding activity "
									+ "or use the exported attribute for this activity. "
									+ " intent=" + intent, e);
		}
	}
	/**
     * Unregister a receiver, but eat the exception that is thrown if the
     * receiver was never registered to begin with. This is a little easier
     * than keeping track of whether the receivers have actually been
     * registered by the time onDestroy() is called.
     */
    public void unregisterReceiverSafe(BroadcastReceiver receiver) {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }
	
	
}
