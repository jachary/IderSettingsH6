package com.rk_itvui.settings.dialog;

import com.zxy.idersettings.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.DisplayOutputManager;
import android.hardware.display.DisplayManager;
import android.preference.SeekBarDialogPreference;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.view.Display;

public class DisplayPercentPreference extends SeekBarDialogPreference implements SeekBar.OnSeekBarChangeListener{
	private DisplayOutputManager mDisplayManager;
	private SeekBar mSeekBar;
	private int OldValue;
	private int MAXIMUM_VALUE = 100;
	 private int MINIMUM_VALUE = 90;
	 private String DISPLAY_AREA_RADIO = "display.area_radio";
	   private String TAG = "DisplayPercentPreference";
	   public static final String DISPLAYOUTPUT_SERVICE = "display_output";
//	@Override
//	protected void onCreate(Bundle arg0) {
//		// TODO Auto-generated method stub
//		super.onCreate(arg0);
//		setContentView(R.layout.preference_dialog_saturation);
//		seekBar = (SeekBar) findViewById(R.id.seekbar);
//		
//			
//
//	}
	   
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromTouch) {
		// TODO Auto-generated method stub
		  setDisplayPercent(progress + MINIMUM_VALUE);
	}
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	public DisplayPercentPreference(Context context,AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
//		mDisplayManager = (DisplayOutputManager)
//                context.getSystemService(Context.DISPLAYOUTPUT_SERVICE);
//		int dispType = mDisplayManager.getDisplayOutputType(Display.TYPE_BUILT_IN);
//		 if(dispType == DisplayOutputManager.DISPLAY_OUTPUT_TYPE_HDMI){
//	        	MAXIMUM_VALUE = 100;
//	        	MINIMUM_VALUE = 95;
//	        } else if(dispType == DisplayOutputManager.DISPLAY_OUTPUT_TYPE_TV){
//	        	MAXIMUM_VALUE = 100;
//	        	MINIMUM_VALUE = 90;
//	        }
	}
	
	   private void setDisplayPercent(int value) {
//	        mDisplayManager.setDisplayMargin(Display.DEFAULT_DISPLAY, value,value);
	    }   
	   
	   private int getDisplayPercent() {
//	        return mDisplayManager.getDisplayMargin(Display.DEFAULT_DISPLAY)[0];
	   return  0;
	   }
	   
	   protected void onBindDialogView(View view) {
	        super.onBindDialogView(view);

	        mSeekBar = getSeekBar(view);
	        mSeekBar.setMax(MAXIMUM_VALUE - MINIMUM_VALUE);
	        OldValue = getDisplayPercent();
	        mSeekBar.setProgress(OldValue - MINIMUM_VALUE);
	        mSeekBar.setOnSeekBarChangeListener(this);
	    }	 
	 
	   protected void onDialogClosed(boolean positiveResult) {
	        if (positiveResult) {
	            setDisplayPercent(mSeekBar.getProgress() + MINIMUM_VALUE);
	        } else {
	            setDisplayPercent(OldValue);
	        }
	        super.onDialogClosed(positiveResult);
	    }

	
	   
}
