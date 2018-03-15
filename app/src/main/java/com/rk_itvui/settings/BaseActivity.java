/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��4��15�� ����4:42:08  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��4��15��      fxw         1.0         create
*******************************************************************/   

package com.rk_itvui.settings;

import com.rk_itvui.settings.WindowHelper;
import com.zxy.idersettings.R;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowHelper.setFullScreen(getWindow());
		getWindow().setBackgroundDrawableResource(R.drawable.background);
	}
	/*
	public Drawable getBackground(){
		return getDrawableFromAssert("main_bg.png");
	}
	
	public Drawable getDrawableFromAssert(String fileName){
		String drawablePath = String.format("drawable-%dx%d/%s", WindowHelper.getWinWidth(this), WindowHelper.getWinHeight(this), fileName);
		if(!new File(drawablePath).exists())
		{
			drawablePath = "drawable/"+fileName;
		}
		//file:///android_asset
		InputStream is = null;
		try {
			is = getAssets().open(drawablePath);
			return Drawable.createFromStream(is, "src");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		
	}
	*/
}
