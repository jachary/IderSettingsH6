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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.DisplayOutputManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.IWindowManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;


public  class ScreensSettings extends FullScreenPreferenceActivity implements OnItemClickListener{

//	private ScreenSetting   displaysetting_local;

	private Context mContext = ScreensSettings.this;
	private String TAG = "Settings2" ;
	private String SubTAG = "--ScreensSettings:" ;
	private DisplayOutputManager mDisplayManager;
	private static final int FALLBACK_DISPLAY_MODE_TIMEOUT = 10;
	private static final int DLG_RESOLUTION_CHANGE_WARNING = 11;
	
	private String setiface , setmode;
	private String iface , vediomode , size ;
	private String resolution_mode = null;
	private int HDMI_TV = -1;
	private int format = 0;
	private String oldValue;
	private String newValue;
	private boolean isSameMode;
	private boolean isSupport = false;
	private boolean dialogTipsFlag;
	String[] mode_entries ;
	String[] mode_value ;
	//int[] mode_value ;
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
		mDisplayManager = (DisplayOutputManager)this.getSystemService(
				Context.DISPLAYOUTPUT_SERVICE);
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
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(this.getPackageName(), "com.rk_itvui.settings.dialog.DisplaySize"));
		startActivity(intent);
		break;
	case 1:
		Log.d("ScreenSettings", "=====================================onclick1");
			//if(isHdmiMode()) {
			//final int hdmi_4k_ban = SystemProperties.getInt("persist.sys.hdmi_4k_ban", 0);
			//if(hdmi_4k_ban == 0){
		mode_entries = res.getStringArray(R.array.hdmi_output_mode_entries);
		mode_value = res.getStringArray(R.array.hdmi_output_mode_values);
//				}else{
//					mode_entries = res.getStringArray(R.array.hdmi_output_mode_without_4k_entries);
//					mode_value = res.getIntArray(R.array.hdmi_output_mode_without_4k_values);
//				}
//			}else{
//				mode_entries = res.getStringArray(R.array.cvbs_output_mode_entries);
//				mode_value = res.getIntArray(R.array.cvbs_output_mode_values);
//			}
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




//boolean plugged = true;//hdmi or cvbs
//private boolean isHdmiMode(){
//	final String filename = "/sys/class/switch/hdmi/state";
//        FileReader reader = null;
//        try {
//            reader = new FileReader(filename);
//            char[] buf = new char[15];
//            int n = reader.read(buf);
//            if (n > 1) {
//                plugged = 0 != Integer.parseInt(new String(buf, 0, n-1));
//            }
//        } catch (IOException ex) {
//			return false;
//        } catch (NumberFormatException ex) {
//			return false;
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException ex) {
//                }
//            }
//        }
//	return plugged;
//}
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
	int currentIndex = 0;
	format  = mDisplayManager.getDisplayOutput(android.view.Display.TYPE_BUILT_IN);
	oldValue = Integer.toHexString(format);
	for(int i = 0;i<mode_value.length;i++){

		if((mode_value[i]+"").equals(oldValue)){
			currentIndex = i;
			break;
		}
	}
	mOutputInterfaceDialog = new AlertDialog.Builder(mContext)
	.setTitle(R.string.screen_interface)
	.setSingleChoiceItems(mode_entries, currentIndex, new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, final int which) {
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					int mode = mode_value[which];
//					i = which;
//					Intent intent = new Intent("com.zxy.display");
//					intent.putExtra("display", mode);
//					intent.putExtra("displayvalue",mode_entries[which]);
//					sendBroadcast(intent);
//				}
//			}, 500);
			//dialog.dismiss();
			Log.d(TAG,SubTAG+"click index is :" +which);
			//switchDispFormat(mode_entries[which]);

			Log.d(TAG,SubTAG + "oldValue is :" + oldValue +"checked value is :" + mode_value[which]);
			newValue = mode_value[which];
			isSameMode = oldValue.equals(mode_value[which]);
			if(isSameMode)
				return ;
			switchDispFormat(mode_value[which]+"");
			//// TODO: 18/3/19 request user dialog
			showDialog(DLG_RESOLUTION_CHANGE_WARNING);
		}
	})
	.setOnCancelListener(new DialogInterface.OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
		Log.d(TAG,SubTAG +" cancel dialog");
			dialog.dismiss();
		}
	}).create();
	mOutputInterfaceDialog.show();
}


	private void switchDispFormat(String value) {
		Log.d(TAG,SubTAG +"switchDispFormat value : " + value);
		try {
			format = Integer.parseInt(value, 16);
			int dispformat = mDisplayManager.getDisplayModeFromFormat(format);
			Log.d(TAG,SubTAG +"disformat is :"+ dispformat);
			int mCurType = mDisplayManager.getDisplayOutputType(android.view.Display.TYPE_BUILT_IN);
			IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.checkService(
					Context.WINDOW_SERVICE));
			int setDispOutputOk = 0;
			int w = 0;
			int h = 0;
			int density = 0;

			isSupport = true;
			if(isSupport){
				Log.w(TAG, "dm set output format:"+format);
				setDispOutputOk = mDisplayManager.setDisplayOutput(android.view.Display.TYPE_BUILT_IN, format);
				Log.w(TAG, "setDisplayOutput return "+setDispOutputOk);
				if(setDispOutputOk == -1 && dialogTipsFlag){
					dialogTipsFlag = !dialogTipsFlag;
					Dialog alertDialog = new AlertDialog.Builder(this)
							.setTitle(this.getResources().getString(R.string.display_support_type_title))
							.setMessage(this.getResources().getString(R.string.display_support_type))
							.setPositiveButton(
									//this.getResources().getString(""),
									"确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
															int which) {
											switchDispFormat(oldValue);
											return ;
										}
									}).create();
					alertDialog.show();
				}
//				mOutputMode.setValue(value);
//				if(mHdmiOutputModePreference!=null)
//					mHdmiOutputModePreference.setValue(value);
				if(0xFF == setDispOutputOk) {
					switch(dispformat) {
						case DisplayOutputManager.DISPLAY_TVFORMAT_3840_2160P_30HZ:
						case DisplayOutputManager.DISPLAY_TVFORMAT_3840_2160P_25HZ:
						case DisplayOutputManager.DISPLAY_TVFORMAT_3840_2160P_24HZ:
						case DisplayOutputManager.DISPLAY_TVFORMAT_1080P_50HZ:
						case DisplayOutputManager.DISPLAY_TVFORMAT_1080P_60HZ:
						case DisplayOutputManager.DISPLAY_TVFORMAT_1080I_60HZ:
						case DisplayOutputManager.DISPLAY_TVFORMAT_1080I_50HZ:
						case DisplayOutputManager.DISPLAY_TVFORMAT_1080P_24HZ:
							w = 1920;
							h = 1080;
							density = 240;
							break;
						case DisplayOutputManager.DISPLAY_TVFORMAT_720P_50HZ:
						case DisplayOutputManager.DISPLAY_TVFORMAT_720P_60HZ:
							w = 1280;
							h = 720;
							density = 160;
							break;
						case DisplayOutputManager.DISPLAY_TVFORMAT_576P:
						case DisplayOutputManager.DISPLAY_TVFORMAT_576I:
							w = 720;
							h = 576;
							density = 128;
							break;
						case DisplayOutputManager.DISPLAY_TVFORMAT_480P:
						case DisplayOutputManager.DISPLAY_TVFORMAT_480I:
							w = 720;
							h = 480;
							density = 106;
							break;
						default:
							setDispOutputOk = 1;
					}
				}
				if(0xFF == setDispOutputOk) {
					if (wm == null) {
						Log.e(TAG,"wm is null!");
					}
					try{
						wm.setForcedDisplaySize(android.view.Display.DEFAULT_DISPLAY, w, h);
						wm.setForcedDisplayDensity(android.view.Display.DEFAULT_DISPLAY, density);
					}catch(RemoteException e){
						Log.d(TAG,"call setForcedDisplaySize error");
					}
				}
			}else {
				//Toast.makeText(getActivity(), com.android.settings.R.string.display_mode_unsupport,Toast.LENGTH_LONG).show();
			}
		} catch (NumberFormatException e) {
			Log.w(TAG, "Invalid display output format!");
		}
	}

	@Override
	public Dialog onCreateDialog(int dialogId) {
		if (DLG_RESOLUTION_CHANGE_WARNING == dialogId) {
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int btn) {
					if (btn == AlertDialog.BUTTON_POSITIVE) {
						switchDispFormat(newValue);
					} else if (btn == AlertDialog.BUTTON_NEGATIVE) {
						switchDispFormat(oldValue);
					}
					dialog.dismiss();
				}
			};

			String str = getString(R.string.display_mode_time_out_desc);
			final AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.display_mode_time_out_title)
					.setMessage(String.format(str, Integer.toString(FALLBACK_DISPLAY_MODE_TIMEOUT)))
					.setPositiveButton(com.android.internal.R.string.ok, listener)
					.setNegativeButton(com.android.internal.R.string.cancel, listener)
					.create();
			dialog.show();

			new AsyncTask(){
				@Override
				protected Object doInBackground(Object... arg0) {
					int time = FALLBACK_DISPLAY_MODE_TIMEOUT;
					while(time >= 0 && dialog.isShowing()){
						publishProgress(time);
						try{
							Thread.sleep(1000);
						}catch(Exception e){}
						time--;
					}
					return null;
				}
				@Override
				protected void onPostExecute(Object result) {
					super.onPostExecute(result);
					if (dialog.isShowing()) {
						Log.d(TAG,"oldValue = " + oldValue);
						switchDispFormat(oldValue);
						dialog.dismiss();
					}
				}
				@Override
				protected void onProgressUpdate(Object... values) {
					super.onProgressUpdate(values);
					int time = (Integer)values[0];
					String str = getString(R.string.display_mode_time_out_desc);
					dialog.setMessage(String.format(str, Integer.toString(time)));
				}
			}.execute();
			return dialog;
		}
		return null;
	}
}
