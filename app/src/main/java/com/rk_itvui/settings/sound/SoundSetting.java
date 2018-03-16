package com.rk_itvui.settings.sound;

import java.util.ArrayList;
import java.util.HashMap;

import com.rk_itvui.settings.FullScreenPreferenceActivity;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.sound.VolumeSettings;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;
import com.rk_itvui.settings.RadioPreference;

import android.R.bool;
import android.R.integer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
//import android.media.R.string;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.os.SystemProperties;;


public class SoundSetting extends FullScreenPreferenceActivity implements OnItemClickListener{
		
	ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		    
	HashMap<String, Object> map_VolumeSetting = new HashMap<String, Object>();
	HashMap<String, Object> map_SoundDeviceManager = new HashMap<String, Object>();
	HashMap<String, Object> map_DolbySetting = new HashMap<String, Object>();
	HashMap<String, Object> map_Audioout = new HashMap<String, Object>();
//	HashMap<String, Object> map_DtsSetting = new HashMap<String, Object>();
	HashMap<String, Object> map_PressVoice = new HashMap<String, Object>();
//	HashMap<String, Object> map_LockVoice = new HashMap<String, Object>();

	SimpleAdapter listItemAdapter;
	private static final String TAG = "Settings2-SoundSetting";
	private VolumeSettings mVolumeSettings = null;
	private AudioManager mAudioManager;
	
	private Context mContext = SoundSetting.this;
	private Handler mUIHandler = null;
	 private TwoStatePreference mTvModePreference;
	private boolean isPressVoiceEnable ;
//	private boolean isLockSoundEnabled ;

    private String mSelectedPlaybackKey;
	public int openmode = 0;
    private static final String KEY_TV_AUDIO_MODE = "tv_audio_mode";
  	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sound_setting_new);
		openmode =Settings.System.getInt(getContentResolver(),Settings.System.TV_AUDIO_MODE,0);
		addListView();
		ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
        
     
	}
	public void appopenmode(){
	
		
	}

	public void addListView() {


        mSelectedPlaybackKey = AudioCommon.getCurrentPlaybackDevice();

        map_VolumeSetting.put("SoundSettingIcon",R.drawable.volume_icon);
		map_VolumeSetting.put("SoundSettingItem", getString(R.string.volume_setting));
		
		map_Audioout.put("SoundSettingIcon",R.drawable.audioout);
		map_Audioout.put("SoundSettingItem", getString(R.string.audioout));

		if (openmode== 1) {
			//Log.d("case1111=== if  openmode", "====" +openmode);
			map_Audioout.put("SoundSettingStatus", getString(R.string.opentheTVmode));


			
		}
		if (openmode== 0) {

			map_Audioout.put("SoundSettingStatus", getString(R.string.closetheTVmode));

		}
		

		
        map_SoundDeviceManager.put("SoundSettingIcon",R.drawable.sound_device_icon);
		map_SoundDeviceManager.put("SoundSettingItem", getString(R.string.audio_device));
        UpdateAudioDeviceDisplay();

        map_DolbySetting.put("SoundSettingIcon",R.drawable.dolby_icon);
		map_DolbySetting.put("SoundSettingItem", getString(R.string.dolby_setting));
		map_DolbySetting.put("SoundSettingStatus",getString(R.string.DolbySettingStatus));



//		map_DtsSetting.put("SoundSettingItem", getString(R.string.DTS_setting));
//		map_DtsSetting.put("SoundSettingStatus",getString(R.string.DTSSettingStatus));
		
		map_PressVoice.put("SoundSettingItem", getString(R.string.press_voice));
		
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		
		if (Settings.System.getInt(SoundSetting.this.getContentResolver(),
				Settings.System.SOUND_EFFECTS_ENABLED, 1) != 0){
			
			
			isPressVoiceEnable = true;
			
		}else{
			
			map_PressVoice.put("SoundSettingStatus",getString(R.string.PressVioceStatus_close));
			isPressVoiceEnable = false;
		}
		
//		map_LockVoice.put("SoundSettingItem", getString(R.string.lock_voice));
				
		
//		if (Settings.System.getInt(SoundSetting.this.getContentResolver(),
//	              Settings.System.LOCKSCREEN_SOUNDS_ENABLED, 1) != 0){
//
//			isLockSoundEnabled = true ;
//			map_LockVoice.put("SoundSettingStatus",getString(R.string.PressVioceStatus_open));
//
//		}else{
//
//			isLockSoundEnabled = false ;
//			map_LockVoice.put("SoundSettingStatus",getString(R.string.PressVioceStatus_close));
//		}
		listItem.add(map_VolumeSetting);
		listItem.add(map_Audioout);
		listItem.add(map_SoundDeviceManager);
		listItem.add(map_DolbySetting);
		
		//listItem.add(map_DtsSetting);
		//listItem.add(map_PressVoice);
		//listItem.add(map_LockVoice);

		ListView list = (ListView) findViewById(R.id.language_list);

		listItemAdapter = new SimpleAdapter(this,
											listItem,				
											R.layout.sound_item,			
											new String[] {"SoundSettingIcon","SoundSettingItem" ,"SoundSettingStatus"},
											new int[] { R.id.SoundSettingIcon,R.id.SoundSettingItem , R.id.SoundSettingStatus});

		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(this);
	}

    public void UpdateAudioDeviceDisplay()
    {
        mSelectedPlaybackKey = AudioCommon.getCurrentPlaybackDevice();

        if(mSelectedPlaybackKey.equals("0")){
            map_SoundDeviceManager.put("SoundSettingStatus", getString(R.string.sound_output_default));
        }else if (mSelectedPlaybackKey.equals("8")){
            map_SoundDeviceManager.put("SoundSettingStatus",getString(R.string.sound_output_spdif_passthrough));
        }else if (mSelectedPlaybackKey.equals("6")) {
            map_SoundDeviceManager.put("SoundSettingStatus",getString(R.string.sound_output_hdmi_passthrough));
        }


    }
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
		Log.d(TAG,"index is ："+ index);
		switch (index) {

		
			case 0:
//				Log.d("smj", "=====================================onclick0");
//				if (mVolumeSettings == null){
//					mVolumeSettings = new VolumeSettings(mContext,mUIHandler);
//				}
//				mVolumeSettings.OnClick();

                Intent intent_volume1 = new Intent(SoundSetting.this, VolumeSetting.class);
                startActivity(intent_volume1);
				break;
			case 1:
//				Intent intent_audioout = new Intent();
//				intent_audioout.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SoundSettingsActivity"));
//                startActivity(intent_audioout);


				if (openmode == 1) {
					Settings.System.putInt(getContentResolver(),Settings.System.TV_AUDIO_MODE,0);
					listItem.get(index).put("SoundSettingStatus",getString(R.string.closetheTVmode));
					openmode = 0;
				}else if (openmode == 0){
					Settings.System.putInt(getContentResolver(),Settings.System.TV_AUDIO_MODE,1);
					listItem.get(index).put("SoundSettingStatus",getString(R.string.opentheTVmode));
					openmode = 1 ;
				}

				listItemAdapter.notifyDataSetChanged();
				break;
			case 2:
				Log.d("smj", "=====================================onclick1");	
				//Intent intent_volume = new Intent(SoundSetting.this, SoundDevicesManager.class);
				//startActivity(intent_volume);
				
				break;
			case 3:
				Log.d("smj", "=====================================onclick1");	
				Toast.makeText(getApplicationContext(),  
						R.string.Dolby_setting_title,
						Toast.LENGTH_SHORT)         				                                         
	               .show(); 
				break;
			case 4:
				Log.d("smj", "=====================================onclick1");	
				
				
				if(isPressVoiceEnable){
					mAudioManager.unloadSoundEffects();
					isPressVoiceEnable = false;
					map_PressVoice.put("SoundSettingStatus",getString(R.string.PressVioceStatus_close));
					
				}else{
					mAudioManager.loadSoundEffects();
					isPressVoiceEnable = true;
					map_PressVoice.put("SoundSettingStatus",getString(R.string.PressVioceStatus_open));

				}
                listItemAdapter.notifyDataSetChanged();
                break;
//			case 4:
//				Log.d("smj", "=====================================onclick1");
//				if(isLockSoundEnabled){
//					Settings.System.putInt(SoundSetting.this.getContentResolver(),
//									Settings.System.LOCKSCREEN_SOUNDS_ENABLED,0);
//					isLockSoundEnabled = false;
//					map_LockVoice.put("SoundSettingStatus",
//								getString(R.string.PressVioceStatus_close));
//					listItemAdapter.notifyDataSetChanged();
//
//				}else{
//					Settings.System.putInt(SoundSetting.this.getContentResolver(),
//							Settings.System.LOCKSCREEN_SOUNDS_ENABLED,1);
//					isLockSoundEnabled = true;
//					map_LockVoice.put("SoundSettingStatus",getString(R.string.PressVioceStatus_open));
//					listItemAdapter.notifyDataSetChanged();
//				}
//				break;
//			case 5:
//				Log.d("smj", "=====================================onclick1");
//				Toast.makeText(getApplicationContext(),
//						R.string.DTS_setting_title,
//						Toast.LENGTH_SHORT)
//	               .show();
//				break;
			default:break;
		
		}
	}	

	@Override
	public void onResume() {

        UpdateAudioDeviceDisplay();
        listItemAdapter.notifyDataSetChanged();
		super.onResume();
	}

}
