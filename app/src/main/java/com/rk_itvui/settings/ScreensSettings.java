package com.rk_itvui.settings;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import com.rk_itvui.settings.dialog.DisplaySize;
import com.rk_itvui.settings.screen.DisplayAdjuestDialog;
import com.rk_itvui.settings.screen.PreferenceManager;
import com.rk_itvui.settings.screen.ScreenScaleActivity;

import com.rk_itvui.settings.screen.ScreenSetting;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;

import com.zxy.idersettings.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;


public  class ScreensSettings extends FullScreenPreferenceActivity implements OnItemClickListener{

//	private ScreenSetting   displaysetting_local;

	private Context mContext = ScreensSettings.this;
	
	private String setiface , setmode;
	private String iface , vediomode , size ;
	private String resolution_mode = null;
	private int HDMI_TV = -1;
	String[] mode_entries ;
	int[] mode_value ;
	HashMap<String, Object> map_dspscaleItem = new HashMap<String, Object>();
	HashMap<String, Object> map_dspmodeItem = new HashMap<String, Object>();
	HashMap<String, Object> map_dspifaceiItem = new HashMap<String, Object>();
	HashMap<String, Object> map_dspvideoscale = new HashMap<String, Object>();
	SimpleAdapter listItemAdapter;
	Resources res;
	private Handler mHandler =new Handler(){
		@Override
		public void handleMessage(Message msg){ 
			switch(msg.what){
			case 0:{
				Log.d("ScreenSettings", "Mode Resume");
//				setmode=displaysetting_local.getCurrentmode();
				if(setiface.equals("TV")){
				if(setmode.equals("720x576i-50"))
					setmode=" PAL";
				else if(setmode.equals("720x480i-60"))
					setmode=" NTSC";
				}
				if (setmode.equals("0x0p-0") || setmode.equals("0x0i-0"))
					setmode = "auto";
				map_dspmodeItem.put("ScreenSettingStatus",setmode);
				listItemAdapter.notifyDataSetChanged();
				vediomode=setmode;
			}
				break;
			case 1:{
				Log.d("ScreenSettings", "Inface Resume");
//				setiface= displaysetting_local.getIfaceTitle(displaysetting_local.getCurrentiface());
//				map_dspifaceiItem.put("ScreenSettingStatus",setiface);
//				if(setiface.equals("HDMI")){
//					resolution_mode = mContext.getString(R.string.resolution);
//					HDMI_TV = R.drawable.dsp3;}
//				else if(setiface.equals("TV")){
//					resolution_mode = mContext.getString(R.string.TV_mode);
//					HDMI_TV = R.drawable.dsp5;}
//				map_dspmodeItem.put("ScreenSettingItem", setiface+" "+resolution_mode);
//				map_dspmodeItem.put("ScreenSettingimg", HDMI_TV);
//				listItemAdapter.notifyDataSetChanged();
			}
				break;
			case 2:{
				Log.d("ScreenSettings", "output Resume FULL");
				map_dspvideoscale.put("ScreenSettingStatus", getString(R.string.default_size));
				listItemAdapter.notifyDataSetChanged();				
			}
				break;
			case 3:{
				Log.d("ScreenSettings", "output Resume 1:1");
				map_dspvideoscale.put("ScreenSettingStatus", getString(R.string.origin_size));
				listItemAdapter.notifyDataSetChanged();				
			}
				break;
			default:
				break;
			}
       		 }
	};	
	PreferenceManager pm ;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_settings);
		addListView();
//		displaysetting_local.getCurrentmode();
		 res =getResources();
		 pm = PreferenceManager.getInstance(this);
		registerReceiver(current, new IntentFilter("com.ider.current"));
	}
	BroadcastReceiver current = new BroadcastReceiver(){
		public void onReceive(Context context, Intent intent) {
			pm.setcurrent(i);
		};
	};
	public void addListView() {
		
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
	
		//=====ScreenSettings=============================================
//		displaysetting_local = new ScreenSetting(mContext,mHandler); 
//		//======================================================
//		
//		vediomode=displaysetting_local.getCurrentmode();
		if ("0x0p-0".equals(vediomode) || "0x0i-0".equals(vediomode))
			vediomode = "auto";
		setmode=vediomode;
//		int temp=displaysetting_local.getCurrentiface();
//		 iface = displaysetting_local.getIfaceTitle(temp);
//		setiface=iface;
		
		String mode_resolution=null;
		int TV_HDMI = -1;
//		if("HDMI".equals(iface)){
//			mode_resolution = mContext.getString(R.string.Resolution);
//			TV_HDMI = R.drawable.dsp3;
//			}
//		else if("TV".equals(iface)){
//			mode_resolution = mContext.getString(R.string.TV_mode);
//			TV_HDMI = R.drawable.dsp5;
//			}
		
		int sizeval = -1;
//		sizeval = displaysetting_local.getCurrentszie();
		if(sizeval==0)
			size = getString(R.string.default_size);
		else
			size = getString(R.string.origin_size);
//		Log.d("ScreenSettings",vediomode );
		//String iface_int="null";
		map_dspscaleItem.put("ScreenSettingItem", getString(R.string.display_scale));
//		map_dspscaleItem.put("ScreenSettingStatus",scale);
		map_dspscaleItem.put("ScreenSettingimg", R.drawable.dsp1);
		
		map_dspifaceiItem.put("ScreenSettingItem", getString(R.string.display_iface));
		map_dspifaceiItem.put("ScreenSettingStatus",iface);
		map_dspifaceiItem.put("ScreenSettingimg", R.drawable.dsp2);

		map_dspmodeItem.put("ScreenSettingItem", iface+" "+mode_resolution);
		map_dspmodeItem.put("ScreenSettingStatus",vediomode);
		map_dspmodeItem.put("ScreenSettingimg", TV_HDMI);
		
		map_dspvideoscale.put("ScreenSettingItem", getString(R.string.output_value));
//		map_dspvideoscale.put("ScreenSettingStatus",size);
		map_dspvideoscale.put("ScreenSettingimg", R.drawable.dsp4);
		
		listItem.add(map_dspscaleItem);
//		listItem.add(map_dspifaceiItem);
//		listItem.add(map_dspmodeItem);
		listItem.add(map_dspvideoscale);

		ListView list = (ListView) findViewById(R.id.display_list);

		listItemAdapter = new SimpleAdapter(this,
												listItem,				
												R.layout.screen_item,			
												new String[] {"ScreenSettingItem" ,"ScreenSettingStatus", "ScreenSettingimg"},
												new int[] { R.id.ScreenSettingItem , R.id.ScreenSettingStatus , R.id.ScreenSettingimg });

		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(this);
	}

	public void startIntentWithTranlete(Activity act,Intent intent){
		ActivityAnimationTool.startActivity(act, intent);
	}

public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
	switch (index) {
	case 0:
		Log.d("ScreenSettings", "=====================================displayscale");
//		Intent intentscale = new Intent(ScreensSettings.this, DisplayPercentPreference.class);
//		startIntentWithTranlete(ScreensSettings.this,intentscale);
////		Intent intentscale1 = new Intent();
////		intentscale1.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DisplaySettingsActivity"));
////        startActivity(intentscale1);
		Intent intent = new Intent();

		intent.setComponent(new ComponentName(this.getPackageName(), "com.rk_itvui.settings.dialog.DisplaySize"));
		startActivity(intent);
		break;
	case 1:
		 Log.d("ScreenSettings", "=====================================onclick1");
			if(isHdmiMode()) {
			final int hdmi_4k_ban = SystemProperties.getInt("persist.sys.hdmi_4k_ban", 0);
			if(hdmi_4k_ban == 0){
				mode_entries = res.getStringArray(R.array.hdmi_output_mode_entries);
				mode_value = res.getIntArray(R.array.hdmi_output_mode_values);
				}else{
					mode_entries = res.getStringArray(R.array.hdmi_output_mode_without_4k_entries);
					mode_value = res.getIntArray(R.array.hdmi_output_mode_without_4k_values);	
				}
			}else{
				mode_entries = res.getStringArray(R.array.cvbs_output_mode_entries);
				mode_value = res.getIntArray(R.array.cvbs_output_mode_values);
			}
			showDisplayDialog();
//			Intent intentscale2 = new Intent();
//			intentscale2.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DisplaySettingsActivity"));
//	        startActivity(intentscale2);
		break;	
	//case 1:
		//Log.d("ScreenSettings", "=====================================onclick1");
		//displaysetting_local.InitIfaceDialog(mContext);
		//displaysetting_local.mOutputInterfaceDialog.show();			
		//break;
//	case 2:
//		Log.d("ScreenSettings", "=====================================onclick2");
//		displaysetting_local.InitModeDialog(mContext);
//		displaysetting_local.mModeDialog.show();
//		try{  
//		    int dividerID=getResources().getIdentifier("android:id/titleDivider", null, null);  
//		    View divider=displaysetting_local.mModeDialog.findViewById(dividerID);  
//		    divider.setBackgroundColor(Color.TRANSPARENT);  
//		}catch(Exception e){  

//		    e.printStackTrace();  
//		} 
//		break;
//	case 3:
//		Log.d("ScreenSettings", "=====================================onclick3");
//		displaysetting_local.InitOutputDialog(mContext);
//		displaysetting_local.mOutputDialog.show();
//		try{  
//		    int dividerID=getResources().getIdentifier("android:id/titleDivider", null, null);  
//		    View divider=displaysetting_local.mOutputDialog.findViewById(dividerID);  
//		    divider.setBackgroundColor(Color.TRANSPARENT);  
//		}catch(Exception e){  

//		    e.printStackTrace();  
//		} 
//		break;
	default:
		break;
		
	}
}




boolean plugged = true;//hdmi or cvbs
private boolean isHdmiMode(){
	final String filename = "/sys/class/switch/hdmi/state";
        FileReader reader = null;
        try {
            reader = new FileReader(filename);
            char[] buf = new char[15];
            int n = reader.read(buf);
            if (n > 1) {
                plugged = 0 != Integer.parseInt(new String(buf, 0, n-1));
            }
        } catch (IOException ex) {
			return false;
        } catch (NumberFormatException ex) {
			return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }
	return plugged;
}
public Dialog mOutputInterfaceDialog = null;


public int setcurrent(String currentdate){
		Log.i("zxydis", "currentdate="+currentdate);
	for(int i =0 ;i<mode_entries.length;i++ ){
		Log.i("zxydis", "mode_entries[i].toLowerCase().trim()=="+mode_entries[i].toLowerCase().trim());
		if(mode_entries[i].toLowerCase().trim().equals(currentdate)){
			return i;
		}
	}
	return 0;
}
int i = 2;
public void showDisplayDialog(){
	mOutputInterfaceDialog = new AlertDialog.Builder(mContext)
	.setTitle(R.string.screen_interface)
	.setSingleChoiceItems(mode_entries, pm.getBootCount(), new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, final int which) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					int mode = mode_value[which];
					i = which;					
					Intent intent = new Intent("com.zxy.display");
					intent.putExtra("display", mode);
					intent.putExtra("displayvalue",mode_entries[which]);
					sendBroadcast(intent);
				}
			}, 500);
			dialog.dismiss();
		}
	})
	.setOnCancelListener(new DialogInterface.OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			
			dialog.dismiss();
		}
	}).create();
	mOutputInterfaceDialog.show();
}

}
