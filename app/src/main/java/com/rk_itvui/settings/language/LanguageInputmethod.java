package com.rk_itvui.settings.language;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.DisplayMetrics;
import com.rk_itvui.settings.FullScreenPreferenceActivity;
import com.rk_itvui.settings.Settings;
import com.zxy.idersettings.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.hardware.input.KeyboardLayout;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.os.SystemProperties;
import com.rk_itvui.settings.ScreenInformation;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;


public class LanguageInputmethod extends FullScreenPreferenceActivity implements OnItemClickListener{
		
	private KeyBoardSetting KeyBoard_local ;
	private LanguageSetting language_local ;
	private Context mContext = LanguageInputmethod.this;
	private Handler mHandler = null;
	private int mId = -1;
    
	HashMap<String, Object> map_LanugageItem = new HashMap<String, Object>();
	HashMap<String, Object> map_InputMethodiItem = new HashMap<String, Object>();
	SimpleAdapter listItemAdapter;
       
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.language_inputmethod);
		addListView();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ScreenInformation.mScreenWidth = displayMetrics.widthPixels;
        ScreenInformation.mScreenHeight = displayMetrics.heightPixels;
        ScreenInformation.mDensityDpi = displayMetrics.densityDpi;
        ScreenInformation.mDpiRatio = ((float) ScreenInformation.mDefaultDpi)/(float)displayMetrics.densityDpi;
        ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
    }

	public void addListView() {
		
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

	
		KeyBoard_local = new KeyBoardSetting(mContext,mHandler,mId);		
		language_local = new LanguageSetting(mContext,mHandler);
	
		map_LanugageItem.put("LanguageSettingIcon",R.drawable.language_icon);
		map_LanugageItem.put("LanguageSettingItem", getString(R.string.language_seletor));
		map_LanugageItem.put("LanguageSettingStatus",language_local.getDefaultLanguageSetting());

        map_InputMethodiItem.put("LanguageSettingIcon",R.drawable.inputmethod_icon);
		map_InputMethodiItem.put("LanguageSettingItem", getString(R.string.input_method_selector));
		map_InputMethodiItem.put("LanguageSettingStatus",KeyBoard_local.getKeyBoardDefault());

		
		listItem.add(map_LanugageItem);
		listItem.add(map_InputMethodiItem);

		ListView list = (ListView) findViewById(R.id.language_list);

		listItemAdapter = new SimpleAdapter(this,
												listItem,				
												R.layout.language_item,			
												new String[] {"LanguageSettingIcon","LanguageSettingItem" ,"LanguageSettingStatus"},
												new int[] { R.id.LanguageSettingIcon,R.id.LanguageSettingItem , R.id.LanguageSettingStatus});

		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(this);
	}
	public void startIntentWithTranlete(Activity act,Intent intent){
		ActivityAnimationTool.startActivity(act, intent);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
		switch (index) {
		case 0:
			Log.d("smj", "=====================================onclick0");
			//language_local.settingLanguage();

           // Intent intent1= new Intent(mContext, LanguageActivity.class);
           // startIntentWithTranlete(LanguageInputmethod.this,intent1);
			break;
		case 1:
			Log.d("smj", "=====================================onclick1");			
			
			Intent intent = new Intent(mContext, KeyBoardSettingAlterDialogActivity.class); 
			startActivity(intent);
			break;
		}
	}
	
	
	@Override
	public void onResume() {
		
		Log.d("DateTimeSetting", "=========Activity:onResume");
		super.onResume();
		
		map_InputMethodiItem.put("LanguageSettingStatus",KeyBoard_local.getKeyBoardDefault());
		listItemAdapter.notifyDataSetChanged();
	}

}
