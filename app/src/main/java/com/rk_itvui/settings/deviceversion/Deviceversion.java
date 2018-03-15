package com.rk_itvui.settings.deviceversion;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Log;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.os.Build;
import android.os.SystemProperties;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.rk_itvui.settings.FullScreenActivity;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.ScreenInformation;
import com.rk_itvui.settings.tranlete.ActivityAnimationTool;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public class Deviceversion extends FullScreenActivity
{
	private int mymKeycodeStackIndex = 0;
	private static final String PRODUCT_VERSION= SystemProperties.get("ro.rksdk.version","rockchip");
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
//		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_CLEARABLE_FLAGS);
        setContentView(R.layout.version);

		//createTitle();
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //ScreenInfo.DENSITY = displayMetrics.densityDpi;
        //ScreenInfo.WIDTH = displayMetrics.widthPixels;
        //ScreenInfo.HEIGHT = displayMetrics.heightPixels;

        ScreenInformation.mScreenWidth = displayMetrics.widthPixels;
        ScreenInformation.mScreenHeight = displayMetrics.heightPixels;
        ScreenInformation.mDensityDpi = displayMetrics.densityDpi;
        ScreenInformation.mDpiRatio = ((float) ScreenInformation.mDefaultDpi)
            / (float) displayMetrics.densityDpi;
		createContextTitle();
		createContext();
		ActivityAnimationTool.prepareAnimation(this);
        ActivityAnimationTool.animate(this, 1000);
    }
	
	final static private int [] KEYCODE_FactoryMode = {KeyEvent.KEYCODE_DPAD_LEFT ,KeyEvent.KEYCODE_DPAD_RIGHT,
		KeyEvent.KEYCODE_DPAD_LEFT ,KeyEvent.KEYCODE_DPAD_RIGHT,KeyEvent.KEYCODE_DPAD_LEFT ,KeyEvent.KEYCODE_DPAD_RIGHT};
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if (keyCode == KEYCODE_FactoryMode[mymKeycodeStackIndex]){
            mymKeycodeStackIndex++;
        }else {
            mymKeycodeStackIndex = 0;
        }
       if (mymKeycodeStackIndex >= KEYCODE_FactoryMode.length){
           mymKeycodeStackIndex = 0;
           
           Log.i("++++", "++++++++++++++++++++++format");
           Intent intent = new Intent("con.ider.overlauncher.SETTINGS");
           sendBroadcast(intent);
           
       }
		
		
		return super.onKeyDown(keyCode, event);
		
		
	}
	private void createContextTitle()
	{
		float size = ScreenInformation.mScreenWidth/52f*ScreenInformation.mDpiRatio;
		TextView modeTitle = (TextView)findViewById(R.id.mode_title);
		modeTitle.setTextSize(size);

		TextView android = (TextView)findViewById(R.id.android_title);
		android.setTextSize(size);

		TextView kernel = (TextView)findViewById(R.id.kernel_title);
		kernel.setTextSize(size);

		TextView build = (TextView)findViewById(R.id.build_title);
		build.setTextSize(size);
		TextView uuid = (TextView) findViewById(R.id.build_title3);
		uuid.setTextSize(size);
		
	}
	private String getUuid(){
        String version = null;
        try {
            Method method = Build.class.getDeclaredMethod("getString", String.class);
            method.setAccessible(true);
            version = (String) method.invoke(new Build(), "persist.service.aligenie.uuid");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    Log.i("version", version);
        return version;
    }
	private void createContext()
	{
		float size = ScreenInformation.mScreenWidth/52f*ScreenInformation.mDpiRatio;

		TextView mode = (TextView)findViewById(R.id.versiondevices);
		mode.setText(Build.MODEL);
		mode.setTextSize(size);

		TextView android = (TextView)findViewById(R.id.versionandroid);
		android.setText(Build.VERSION.RELEASE);
		android.setTextSize(size);

		TextView kernel = (TextView)findViewById(R.id.versionkernel);
		kernel.setText(getFormattedKernelVersion());
		kernel.setTextSize(size);

		TextView build = (TextView)findViewById(R.id.numberversion);
//		build.setText(PRODUCT_VERSION + "\n" + Build.DISPLAY);
		build.setText( Build.DISPLAY);
		build.setTextSize(size);
		
		TextView uuid = (TextView) findViewById(R.id.numberversion3);
		uuid.setText(getUuid());
		uuid.setTextSize(size);
	}

	/*private void createTitle()
	{
		ImageView image = (ImageView)findViewById(R.id.imageView);
		Bitmap resize = bitMapScale(R.drawable.product_model);
		image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		image.setImageBitmap(resize);

		//TextView title = (TextView)findViewById(R.id.display_text);
		//title.setTextSize(ScreenInformation.mScreenWidth/25f*ScreenInformation.mDpiRatio);
	}*/

	private Bitmap bitMapScale(int id)
	{
		Bitmap map = BitmapFactory.decodeResource(this.getResources(),id);
		float scale = ScreenInformation.mScreenWidth/1280f*ScreenInformation.mDpiRatio;
		int width = (int)((float)map.getWidth()*scale);
		int height = (int)((float)map.getHeight()*scale);

 		Bitmap resize = Bitmap.createScaledBitmap(map, width, height, true);
		return resize;
	}

	private String getFormattedKernelVersion() {
		String procVersionStr;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"/proc/version"), 256);
			try {
				procVersionStr = reader.readLine();
			} finally {
				reader.close();
			}

			final String PROC_VERSION_REGEX = "\\w+\\s+" + /* ignore: Linux */
			"\\w+\\s+" + /* ignore: version */
			"([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
			"\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /*
														 * group 2:
														 * (xxxxxx@xxxxx
														 * .constant)
														 */
			"\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
			"([^\\s]+)\\s+" + /* group 3: #26 */
			"(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
			"(.+)"; /* group 4: date */

			Pattern p = Pattern.compile(PROC_VERSION_REGEX);
			Matcher m = p.matcher(procVersionStr);

			if (!m.matches()) {
				return "Unavailable";
			} else if (m.groupCount() < 4) {
				return "Unavailable";
			} else {
				return (new StringBuilder(m.group(1)).append("\n")
						.append(m.group(2)).append(" ").append(m.group(3))
						.append("\n").append(m.group(4))).toString();
			}
		} catch (IOException e) {
			return "Unavailable";
		}
	}
}
