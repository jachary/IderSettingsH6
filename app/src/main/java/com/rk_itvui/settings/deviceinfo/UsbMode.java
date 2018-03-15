package com.rk_itvui.settings.deviceinfo;

import com.zxy.idersettings.R;
import com.rk_itvui.settings.developer.DevelopmentSettings;
import com.rk_itvui.settings.developer.SettingMacroDefine;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

public class UsbMode {
	private static final String TAG = "UsbMode";
	private static final boolean DEBUG = true;
	private void LOG(String msg){
		if (DEBUG) {
			Log.d(TAG,msg);
		}
	}
	
	private Context mContext = null;
	private Handler mUIHandler = null;
	private Resources mRes = null;
	
	public static final int REQUEST_CODE_USB_CONNECT_MODE = 1002;
	
	public static final String USB_MODE = "usb_mode";
	
	private static final String KEY_MTP = "usb_mtp";
	private static final String KEY_PTP = "usb_ptp";
	private static final String KEY_MASS = "usb_mass";

    private UsbManager mUsbManager;
	
	public UsbMode(Context context,Handler handler){
		mContext = context;
		mUIHandler = handler;
		mUsbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
	}
	
	public void Resume(){
//		String function = mUsbManager.getDefaultFunction();
//		if (UsbManager.USB_FUNCTION_MTP.equals(function)) {
//			((DevelopmentSettings) mContext).updateSettingItem(R.string.storage_menu_usb, R.string.usb_mtp_title, -1, R.drawable.mtp);
//        } else if (UsbManager.USB_FUNCTION_PTP.equals(function)) {
//        	((DevelopmentSettings) mContext).updateSettingItem(R.string.storage_menu_usb, R.string.usb_ptp_title, -1, R.drawable.mtp);
//        } else if (UsbManager.USB_FUNCTION_MASS_STORAGE.equals(function)) {
//        	((DevelopmentSettings) mContext).updateSettingItem(R.string.storage_menu_usb, R.string.usb_mass_title, -1, R.drawable.mtp);
//        }  else  {
//
//        }
//		mUIHandler.sendEmptyMessage(SettingMacroDefine.upDateListView);
	}
	
	public void Pause(){
		
	}
	
	public void updateMode(String key){
		if (key == null)
			return;
		LOG("usbmode:"+key);
		if (key.equals(KEY_MTP)){
			((DevelopmentSettings) mContext).updateSettingItem(R.string.storage_menu_usb, R.string.usb_mtp_title, -1, R.drawable.mtp);
		} else if (key.equals(KEY_PTP)){
			((DevelopmentSettings) mContext).updateSettingItem(R.string.storage_menu_usb, R.string.usb_ptp_title, -1, R.drawable.mtp);
		} else if (key.equals(KEY_MASS)){
			((DevelopmentSettings) mContext).updateSettingItem(R.string.storage_menu_usb, R.string.usb_mass_title, -1, R.drawable.mtp);
		}
		
		mUIHandler.sendEmptyMessage(SettingMacroDefine.upDateListView);
	}
}
