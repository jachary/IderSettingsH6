package com.rk_itvui.settings.screen;

//import com.ider.background.ConfigService;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PreferenceManager {
	Context context;
	private static SharedPreferences preferences;
	private static PreferenceManager manager;
	Editor editor;
	
	public static PreferenceManager getInstance(Context context) {
		if(manager == null) {
			manager = new PreferenceManager(context);
		}
		return manager;
	}

	private PreferenceManager(Context context) {
		this.context = context;
		preferences = context.getSharedPreferences("fla", Context.MODE_PRIVATE);
		editor = preferences.edit();
	}

	// 锟斤拷位锟斤拷锟斤拷=============================
	public  void putString(String tag, String pkgName) {
		if (preferences.getString(tag, null) != null) {
			editor.remove(tag);
			editor.commit();
		}
		editor.putString(tag, pkgName);
		editor.commit();
	}

	public void delete(String tag) {
		editor.remove(tag);
		editor.commit();
	}
	
	public synchronized String getPackage(String tag) {
		String packageName = preferences.getString(tag, null);
		return packageName;
	}
//	public String gettag(String pack) {
//		String tag = preferences.getString(pack, null);
//		if(tag==null){
//			tag=null;
//		}
//		return tag;
//	}

	public boolean isFirstRun() {
		return preferences.getBoolean("first_run", true);
	}

	public void setFirstRun(boolean firstRun) {
		editor.putBoolean("first_run", firstRun);
		editor.commit();
	}
	
	public void putBoolean(String key, boolean b) {
		editor.putBoolean(key, b);
		editor.commit();
	}
	
	
	public boolean getBoolean(String key) {
		return preferences.getBoolean(key, true);
	}
	
	
	// 一锟斤拷直锟斤拷锟接︼拷锟斤拷锟�===========================
	public String getKeyPackage(int keycode) {
		String key = String.valueOf(keycode);
		return preferences.getString(key, null);
	}
	
	public void setKeyPackage(int keycode, String packageName) {
		editor.putString(String.valueOf(keycode), packageName);
		editor.commit();
	}
	
	public void removeKeyPackage(int keycode) {
		editor.remove(String.valueOf(keycode));
		editor.commit();
	}
	// 一锟斤拷直锟斤拷锟接︼拷锟斤拷锟�===========================
	

//	public boolean isFirstRun() {
//		return preferences.getBoolean("first_run", true);
//	}
//	
//	public void setFirstRun(boolean firstRun) {
//		editor.putBoolean("first_run", firstRun);
//		editor.commit();
//	}
	
	
	//===================锟斤拷锟铰猴拷锟揭伙拷锟斤拷锟斤拷锟�======================
	public boolean cacheDeleted() {
		return preferences.getBoolean("cache_removed", false);
	}
	public void setCacheDeleted() {
		editor.putBoolean("cache_removed", true);
		editor.commit();
	}
	//===================锟斤拷锟铰猴拷锟揭伙拷锟斤拷锟斤拷锟�======================
	
	
	//===================锟皆讹拷锟斤拷锟斤拷锟�====================
	public String getManuCity() {
		return preferences.getString("city", null);
	}
	public void setManuCity(String city) {
		if(getManuCity() != null) {
			removeCity();
		}
		editor.putString("city", city);
		editor.commit();
	}
	public void removeCity() {
		editor.remove("city");
		editor.commit();
	}
	//===================锟皆讹拷锟斤拷锟斤拷锟�====================
	
	
	//==================锟斤拷锟斤拷锟斤拷锟斤拷=====================
	public void setBootPackage(String pkg) {
		editor.putString("boot_package", pkg);
		editor.commit();
	}
	public void removeBootPackage() {
		editor.remove("boot_package");
		editor.commit();
	}
	public String getBootPackage() {
		return preferences.getString("boot_package", null);
	}
	//==================锟斤拷锟斤拷锟斤拷锟斤拷=====================
	
	
	
	
	//========================锟斤拷锟斤拷锟斤拷锟�=========================
	public int getBootCount() {
		return preferences.getInt("boot_count", 2);
	}
	
	public void setcurrent(int x) {
		editor.putInt("boot_count",x);
		editor.commit();
	}
	//========================锟斤拷锟斤拷锟斤拷锟�=========================



	public int getLocalServiceVersion() {
		return preferences.getInt("config_service", -1);
	}

	public void setLocalServiceVersion(int version) {
		editor.putInt("config_service", version);
		editor.commit();
	}



	// =======================鏄惁寮�鍚悗鍙伴厤缃�==========================
	public boolean ifConfigServiceOn() {
		return preferences.getBoolean("is_config_on", false);
	}
	public void setConfigServiceOn() {
		editor.putBoolean("is_config_on", true);
		editor.commit();
	}
	public String getConfigLocation() {
		return preferences.getString("config_location", null);
	}
	public void setConfigLocation(String location) {
		editor.putString("config_location", location);
		editor.commit();
	}




}
	
	


