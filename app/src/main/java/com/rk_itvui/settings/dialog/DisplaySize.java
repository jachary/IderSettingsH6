package com.rk_itvui.settings.dialog;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.DisplayOutputManager;
import android.telecom.Log;
import android.view.Display;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zxy.idersettings.R;

public class DisplaySize extends Activity implements SeekBar.OnSeekBarChangeListener{
	private DisplayOutputManager mDisplayManager;
	private SeekBar mSeekBar;
	private TextView mTextView;
	private int OldValue;
	private int MAXIMUM_VALUE = 100;
	private int MINIMUM_VALUE = 90;
	private String DISPLAY_AREA_RADIO = "display.area_radio";
	private String TAG = "DisplayPercentPreference";
	public static final String DISPLAYOUTPUT_SERVICE = "display_output";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_size);
		mSeekBar = (SeekBar) findViewById(R.id.dis_seekBar);
		mTextView = (TextView) findViewById(R.id.dis_tv);
		mDisplayManager = (DisplayOutputManager) this.getSystemService(Context.DISPLAYOUTPUT_SERVICE);
		int dispType = mDisplayManager.getDisplayOutputType(Display.TYPE_BUILT_IN);
		if(dispType == DisplayOutputManager.DISPLAY_OUTPUT_TYPE_HDMI){
			MAXIMUM_VALUE = 100;
			MINIMUM_VALUE = 95;
		} else if(dispType == DisplayOutputManager.DISPLAY_OUTPUT_TYPE_TV){
			MAXIMUM_VALUE = 100;
			MINIMUM_VALUE = 90;
		}

		Log.d(TAG,"MAXIMUM_VALUE is :"+ MAXIMUM_VALUE+ ";MINIMUM_VALUE value is :"+ MINIMUM_VALUE);
		mSeekBar.setMax(MAXIMUM_VALUE - MINIMUM_VALUE);
		OldValue = getDisplayPercent();
		mSeekBar.setProgress(OldValue - MINIMUM_VALUE);
		mTextView.setText((MAXIMUM_VALUE - MINIMUM_VALUE)*100/(OldValue - MINIMUM_VALUE)+"%");
		mSeekBar.setOnSeekBarChangeListener(this);
	}






	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromTouch) {
		// TODO Auto-generated method stub
		  setDisplayPercent(progress + MINIMUM_VALUE);
		mTextView.setText(progress + MINIMUM_VALUE+"%");
	}

	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			setDisplayPercent(mSeekBar.getProgress() + MINIMUM_VALUE);
		} else {
			setDisplayPercent(OldValue);
		}
		//super.onDialogClosed(positiveResult);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

    private void setDisplayPercent(int value) {
		mDisplayManager.setDisplayMargin(Display.DEFAULT_DISPLAY, value,value);
	}
	   
	private int getDisplayPercent() {
		return mDisplayManager.getDisplayMargin(Display.DEFAULT_DISPLAY)[0];

	}

}
